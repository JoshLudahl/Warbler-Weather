package com.warbler.ui.main

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.utilities.ConnectivityObserver
import com.warbler.core.utilities.DataPref
import com.warbler.core.utilities.Resource
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.convertOrReturnAccumulationByUnit
import com.warbler.data.model.weather.Conversion.fromHourWithSuffix
import com.warbler.data.model.weather.WeatherCacheEntity
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.data.repositories.weather.WeatherDatabaseRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.feature.weather.ui.main.DailyForecastItem
import com.warbler.feature.weather.ui.main.HourlyForecastItem
import com.warbler.feature.weather.ui.main.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainWeatherViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val weatherNetworkRepository: WeatherNetworkRepository,
        private val weatherDatabaseRepository: WeatherDatabaseRepository,
        dataStore: DataStore<Preferences>,
        private val connectivityObserver: ConnectivityObserver,
    ) : ViewModel() {
        private val currentWeatherData = MutableStateFlow<WeatherDataSource?>(null)
        private val currentAqiLevel = MutableStateFlow(0)
        private val lastRefreshedAt = MutableStateFlow<String?>(null)
        private val _isOffline = MutableStateFlow(false)

        val isOffline: StateFlow<Boolean> = _isOffline

        private val unitSettingsFlow =
            combine(
                DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore),
                DataPref.readIntDataStoreFlow(DataPref.SPEED_UNIT, dataStore),
                DataPref.readIntDataStoreFlow(DataPref.CLOCK_UNIT, dataStore),
                DataPref.readIntDataStoreFlow(DataPref.ACCUMULATION_UNIT, dataStore),
            ) { temp, speed, clock, accum ->
                UnitSettings(temp, speed, clock, accum)
            }

        val weatherUiState: StateFlow<WeatherUiState?> =
            combine(
                combine(currentWeatherData, currentAqiLevel, _isOffline) { d, a, o -> Triple(d, a, o) },
                combine(
                    lastRefreshedAt,
                    locationRepository.getCurrentLocationFromDatabase(),
                    unitSettingsFlow,
                ) { r, l, u -> Triple(r, l, u) },
            ) { t1, t2 ->
                val (data, aqi, offline) = t1
                val (refreshed, location, units) = t2

                data
                    ?.toUiState(
                        location,
                        units.temperatureUnit,
                        units.speedUnit,
                        units.clockUnit,
                        units.accumulationUnit,
                        aqi,
                    )?.copy(
                        isOffline = offline,
                        lastRefreshedAt = if (offline) refreshed else null,
                    )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        private var loadWeatherJob: Job? = null
        private var isConnected = false
        private val json = Json { ignoreUnknownKeys = true }

        data class UnitSettings(
            val temperatureUnit: Int,
            val speedUnit: Int,
            val clockUnit: Int,
            val accumulationUnit: Int,
        )

        init {
            viewModelScope.launch {
                combine(
                    connectivityObserver.observe(),
                    locationRepository.getCurrentLocationFromDatabase(),
                ) { status, location ->
                    status to location
                }.collect { (status, location) ->
                    Log.d(TAG, "Connectivity status: $status")
                    isConnected = status == ConnectivityObserver.Status.Available
                    // Coming back online clears the banner up front. It only returns if every
                    // attempt against the network fails and we fall back to the cache.
                    _isOffline.value = !isConnected
                    loadWeather(location, forceCache = !isConnected)
                }
            }
        }

        private fun loadWeather(
            location: LocationEntity,
            forceCache: Boolean = false,
        ) {
            loadWeatherJob?.cancel()
            loadWeatherJob =
                viewModelScope.launch {
                    try {
                        val cache = weatherDatabaseRepository.getWeatherCache(location.lat, location.lon)
                        val isCacheFresh =
                            cache?.let {
                                Duration
                                    .between(Instant.ofEpochMilli(it.lastUpdated), Instant.now())
                                    .toMinutes() < CACHE_LIFETIME_MINUTES
                            } ?: false

                        if (forceCache || isCacheFresh) {
                            cache?.let { showCachedWeather(it) }
                            return@launch
                        }

                        weatherNetworkRepository
                            .getCurrentWeather(location)
                            .retryWhen { cause, attempt ->
                                // A network that has just come back often fails the first request
                                // or two. Retrying keeps a transient failure from latching the
                                // offline banner on for a user who is actually online.
                                val shouldRetry = isConnected && attempt < MAX_NETWORK_RETRIES
                                if (shouldRetry) {
                                    Log.w(TAG, "Weather fetch failed, retrying: ${cause.message}")
                                    delay((RETRY_DELAY_MS * (attempt + 1)).milliseconds)
                                }
                                shouldRetry
                            }.catch { e ->
                                Log.e(TAG, "Error fetching weather: ${e.message}")
                                cache?.let { showCachedWeather(it) }
                                _isOffline.value = true
                            }.collect { weather ->
                                val weatherJson = json.encodeToString(WeatherDataSource.serializer(), weather)
                                weatherDatabaseRepository.insertWeatherCache(
                                    WeatherCacheEntity(
                                        lat = location.lat,
                                        lon = location.lon,
                                        json = weatherJson,
                                        lastUpdated = System.currentTimeMillis(),
                                    ),
                                )

                                lastRefreshedAt.value = formatLastUpdated(System.currentTimeMillis())
                                currentWeatherData.value = weather
                                _isOffline.value = false
                            }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.e(TAG, "Unexpected error: ${e.message}")
                    }
                }
            if (!forceCache) loadAqi(location)
        }

        private fun showCachedWeather(cache: WeatherCacheEntity) {
            lastRefreshedAt.value = formatLastUpdated(cache.lastUpdated)
            currentWeatherData.value = json.decodeFromString<WeatherDataSource>(cache.json)
        }

        private fun formatLastUpdated(timestamp: Long): String =
            DateTimeFormatter
                .ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(Instant.ofEpochMilli(timestamp))

        private fun loadAqi(location: LocationEntity) {
            viewModelScope.launch {
                weatherNetworkRepository
                    .getCurrentAqi(location)
                    .catch { }
                    .collect { resource ->
                        if (resource is Resource.Success) {
                            val aqiLevel =
                                resource.data.list
                                    .firstOrNull()
                                    ?.main
                                    ?.aqi ?: 0
                            currentAqiLevel.value = aqiLevel
                        }
                    }
            }
        }

        private fun WeatherDataSource.toUiState(
            location: LocationEntity,
            temperatureUnit: Int,
            speedUnit: Int,
            clockUnit: Int,
            accumulationUnit: Int,
            aqiLevel: Int,
        ): WeatherUiState =
            WeatherUiState(
                locationName = location.toDisplayString,
                temperature = convertTemperature(current.temp, temperatureUnit),
                description =
                    current.weather
                        .firstOrNull()
                        ?.description
                        ?.capitalizeEachFirst
                        .orEmpty(),
                dateTitle = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()),
                feelsLike = "Feels like ${convertTemperature(current.feelsLike, temperatureUnit)}",
                hasAqi = aqiLevel in 1..5,
                aqiValue = aqiLevel.toString(),
                aqiLevel = aqiLevel,
                hasAlerts = !alerts.isNullOrEmpty(),
                alertTitle = alerts?.firstOrNull()?.event.orEmpty(),
                alertDescription = alerts?.joinToString("\n\n") { it.description }.orEmpty(),
                iconRes =
                    current.weather
                        .firstOrNull()
                        ?.icon
                        .orEmpty()
                        .getIconForCondition,
                wind =
                    Conversion.formatSpeedUnitsWithUnitsToString(
                        current.windSpeed,
                        SpeedUnit.entries[speedUnit],
                    ),
                windUnit =
                    when (SpeedUnit.entries[speedUnit]) {
                        SpeedUnit.MPH -> "MPH"
                        SpeedUnit.MPS -> "m/s"
                        SpeedUnit.KPH -> "KMH"
                    },
                humidity = "${current.humidity}%",
                rain = "${(hourly.firstOrNull()?.pop?.times(100))?.toInt() ?: 0}%",
                accumulationUnit =
                    when (AccumulationUnit.entries[accumulationUnit]) {
                        AccumulationUnit.INCHES_PER_HOUR -> "in/h"
                        AccumulationUnit.MILLIMETERS_PER_HOUR -> "mm/h"
                    },
                hourlyForecasts =
                    hourly.take(48).map {
                        val hour =
                            Instant
                                .ofEpochSecond(it.dt.toLong() + timezoneOffset)
                                .atZone(ZoneId.of("UTC"))
                                .hour

                        val timeLabel =
                            if (clockUnit == 1) {
                                String.format(Locale.getDefault(), "%02d:00", hour)
                            } else {
                                hour.fromHourWithSuffix
                            }

                        val hourlyAccumulation = (it.rain?.h ?: 0.0) + (it.snow?.h ?: 0.0)

                        HourlyForecastItem(
                            time = timeLabel,
                            temperature = convertTemperature(it.temp, temperatureUnit),
                            iconRes =
                                it.weather
                                    .firstOrNull()
                                    ?.icon
                                    .orEmpty()
                                    .getIconForCondition,
                            pop = it.pop.toFloat(),
                            accumulation =
                                convertOrReturnAccumulationByUnit(
                                    hourlyAccumulation,
                                    AccumulationUnit.entries[accumulationUnit],
                                ).toFloat(),
                            rainAccumulation =
                                convertOrReturnAccumulationByUnit(
                                    it.rain?.h ?: 0.0,
                                    AccumulationUnit.entries[accumulationUnit],
                                ).toFloat(),
                            snowAccumulation =
                                convertOrReturnAccumulationByUnit(
                                    it.snow?.h ?: 0.0,
                                    AccumulationUnit.entries[accumulationUnit],
                                ).toFloat(),
                            humidity = it.humidity,
                            windSpeed =
                                Conversion
                                    .formatSpeedUnitsWithUnits(
                                        it.windSpeed,
                                        SpeedUnit.entries[speedUnit],
                                    ).roundToInt()
                                    .toFloat(),
                            windGust =
                                Conversion
                                    .formatSpeedUnitsWithUnits(
                                        it.windGust,
                                        SpeedUnit.entries[speedUnit],
                                    ).roundToInt()
                                    .toFloat(),
                            uvi = it.uvi.toFloat(),
                        )
                    },
                dailyForecasts =
                    daily.mapIndexed { index, dailyWeather ->
                        val day =
                            when (index) {
                                0 -> "Today"
                                1 -> "Tomorrow"
                                else -> Conversion.getDatOfWeekFromUnixUTC(dailyWeather.dt.toLong())
                            }
                        DailyForecastItem(
                            day = day,
                            dateTitle =
                                SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(
                                    Date(dailyWeather.dt.toLong() * 1000),
                                ),
                            highTemp = convertTemperatureToInt(dailyWeather.temp.max, temperatureUnit),
                            lowTemp = convertTemperatureToInt(dailyWeather.temp.min, temperatureUnit),
                            iconRes =
                                dailyWeather.weather
                                    .firstOrNull()
                                    ?.icon
                                    .orEmpty()
                                    .getIconForCondition,
                            description =
                                dailyWeather.weather
                                    .firstOrNull()
                                    ?.description
                                    ?.capitalizeEachFirst
                                    .orEmpty(),
                            sunrise =
                                Conversion.getTimeFromTimeStamp(
                                    dailyWeather.sunrise.toLong(),
                                    timezoneOffset.toLong(),
                                    clockUnit,
                                ),
                            sunset =
                                Conversion.getTimeFromTimeStamp(
                                    dailyWeather.sunset.toLong(),
                                    timezoneOffset.toLong(),
                                    clockUnit,
                                ),
                            moonrise =
                                Conversion.getTimeFromTimeStamp(
                                    dailyWeather.moonrise.toLong(),
                                    timezoneOffset.toLong(),
                                    clockUnit,
                                ),
                            moonset =
                                Conversion.getTimeFromTimeStamp(
                                    dailyWeather.moonset.toLong(),
                                    timezoneOffset.toLong(),
                                    clockUnit,
                                ),
                            humidity = "${dailyWeather.humidity}%",
                            wind =
                                Conversion.formatSpeedUnitsWithUnitsToString(
                                    dailyWeather.windSpeed ?: 0.0,
                                    SpeedUnit.entries[speedUnit],
                                ),
                            pop = "${(dailyWeather.pop * 100).toInt()}%",
                            uvIndex = dailyWeather.uvi.toString(),
                            pressure = "${dailyWeather.pressure} hPa",
                            rain =
                                dailyWeather.rain?.let {
                                    "${
                                        convertOrReturnAccumulationByUnit(
                                            it,
                                            AccumulationUnit.entries[accumulationUnit],
                                        )
                                    } ${
                                        if (AccumulationUnit.entries[accumulationUnit] == AccumulationUnit.INCHES_PER_HOUR) "in" else "mm"
                                    }"
                                } ?: "0",
                            snow =
                                dailyWeather.snow?.let {
                                    "${
                                        convertOrReturnAccumulationByUnit(
                                            it,
                                            AccumulationUnit.entries[accumulationUnit],
                                        )
                                    } ${
                                        if (AccumulationUnit.entries[accumulationUnit] == AccumulationUnit.INCHES_PER_HOUR) "in" else "mm"
                                    }"
                                } ?: "0",
                            clouds = "${dailyWeather.clouds}%",
                            dewPoint = convertTemperature(dailyWeather.dewPoint, temperatureUnit),
                        )
                    },
                uvIndex = current.uvi.toString(),
                pressure = "${current.pressure} hPa",
                visibility = "${(current.visibility ?: 0) / 1000} km",
                clouds = "${current.clouds}%",
                dewPoint = convertTemperature(current.dewPoint, temperatureUnit),
                sunrise =
                    Conversion.getTimeFromTimeStamp(
                        current.sunrise.toLong(),
                        timezoneOffset.toLong(),
                        clockUnit,
                    ),
                sunset =
                    Conversion.getTimeFromTimeStamp(
                        current.sunset.toLong(),
                        timezoneOffset.toLong(),
                        clockUnit,
                    ),
            )

        private fun convertTemperature(
            tempKelvin: Double,
            temperatureUnit: Int,
        ): String =
            when (temperatureUnit) {
                0 -> "${(tempKelvin - 273.15).toInt()}°C" // Celsius
                1 -> "${((tempKelvin - 273.15) * 9 / 5 + 32).toInt()}°F" // Fahrenheit
                2 -> "${tempKelvin.toInt()}K" // Kelvin
                else -> "${((tempKelvin - 273.15) * 9 / 5 + 32).toInt()}°F" // Default to Fahrenheit
            }

        private fun convertTemperatureToInt(
            tempKelvin: Double,
            temperatureUnit: Int,
        ): Int =
            when (temperatureUnit) {
                0 -> (tempKelvin - 273.15).toInt() // Celsius
                1 -> ((tempKelvin - 273.15) * 9 / 5 + 32).toInt() // Fahrenheit
                2 -> tempKelvin.toInt() // Kelvin
                else -> ((tempKelvin - 273.15) * 9 / 5 + 32).toInt() // Default to Fahrenheit
            }

        private companion object {
            const val TAG = "MainWeatherViewModel"
            const val CACHE_LIFETIME_MINUTES = 5
            const val MAX_NETWORK_RETRIES = 2
            const val RETRY_DELAY_MS = 1_000L
        }
    }
