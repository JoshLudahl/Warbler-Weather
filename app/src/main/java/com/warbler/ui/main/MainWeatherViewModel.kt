package com.warbler.ui.main

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.utilities.DataPref
import com.warbler.core.utilities.Resource
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
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
                val weatherFlow = weatherNetworkRepository.getCurrentWeather(location).catch { }
                combine(
                    weatherFlow,
                    temperatureUnitFlow,
                    speedUnitFlow,
                ) { weather, temperatureUnit, speedUnit ->
                    weather.toUiState(location, temperatureUnit, speedUnit)
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
                humidity = "${current.humidity}%",
                rain = "${(hourly.firstOrNull()?.pop?.times(100))?.toInt() ?: 0}%",
                hourlyForecasts =
                    hourly.take(48).map {
                        val hour =
                            Instant
                                .ofEpochSecond(it.dt.toLong() + timezoneOffset)
                                .atZone(ZoneId.of("UTC"))
                                .hour

                        HourlyForecastItem(
                            time = hour.fromHourWithSuffix,
                            temperature = convertTemperature(it.temp, temperatureUnit),
                            iconRes =
                                it.weather
                                    .firstOrNull()
                                    ?.icon
                                    .orEmpty()
                                    .getIconForCondition,
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
                        )
                    },
            )
    }
