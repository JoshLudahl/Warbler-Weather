package com.warbler.ui.main

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.utilities.DataPref
import com.warbler.core.utilities.Resource
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.convertOrReturnAccumulationByUnit
import com.warbler.data.model.weather.Conversion.fromHourWithSuffix
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.feature.weather.ui.main.DailyForecastItem
import com.warbler.feature.weather.ui.main.HourlyForecastItem
import com.warbler.feature.weather.ui.main.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainWeatherViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val weatherNetworkRepository: WeatherNetworkRepository,
        private val dataStore: DataStore<Preferences>,
    ) : ViewModel() {
        val weatherUiState: StateFlow<WeatherUiState?>
            field = MutableStateFlow<WeatherUiState?>(null)

        private var pendingAqiLevel: Int? = null

        init {
            viewModelScope.launch {
                locationRepository
                    .getCurrentLocationFromDatabase()
                    .catch { }
                    .collect { location ->
                        loadWeather(location)
                    }
            }
        }

        private fun loadWeather(location: LocationEntity) {
            viewModelScope.launch {
                val temperatureUnitFlow = DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore)
                val speedUnitFlow = DataPref.readIntDataStoreFlow(DataPref.SPEED_UNIT, dataStore)
                val clockUnitFlow = DataPref.readIntDataStoreFlow(DataPref.CLOCK_UNIT, dataStore)
                val accumulationUnitFlow = DataPref.readIntDataStoreFlow(DataPref.ACCUMULATION_UNIT, dataStore)
                val weatherFlow = weatherNetworkRepository.getCurrentWeather(location).catch { }
                combine(
                    weatherFlow,
                    temperatureUnitFlow,
                    speedUnitFlow,
                    clockUnitFlow,
                    accumulationUnitFlow,
                ) { weather, temperatureUnit, speedUnit, clockUnit, accumulationUnit ->
                    weather.toUiState(location, temperatureUnit, speedUnit, clockUnit, accumulationUnit)
                }.collect { uiState ->
                    weatherUiState.value = uiState
                }
            }
            loadAqi(location)
        }

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
                            pendingAqiLevel = aqiLevel
                            weatherUiState.value =
                                weatherUiState.value?.copy(
                                    hasAqi = aqiLevel in 1..5,
                                    aqiValue = aqiLevel.toString(),
                                    aqiLevel = aqiLevel,
                                )
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
                hasAqi = (weatherUiState.value?.aqiLevel ?: pendingAqiLevel ?: 0) in 1..5,
                aqiValue = (weatherUiState.value?.aqiLevel ?: pendingAqiLevel ?: 0).toString(),
                aqiLevel = weatherUiState.value?.aqiLevel ?: pendingAqiLevel ?: 0,
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
                                ).roundToInt().toFloat(),
                            rainAccumulation =
                                convertOrReturnAccumulationByUnit(
                                    it.rain?.h ?: 0.0,
                                    AccumulationUnit.entries[accumulationUnit],
                                ).roundToInt().toFloat(),
                            snowAccumulation =
                                convertOrReturnAccumulationByUnit(
                                    it.snow?.h ?: 0.0,
                                    AccumulationUnit.entries[accumulationUnit],
                                ).roundToInt().toFloat(),
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
    }
