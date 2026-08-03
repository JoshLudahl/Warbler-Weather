package com.warbler.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.model.location.LocationEntity
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.feature.weather.ui.main.WeatherUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainWeatherViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val weatherNetworkRepository: WeatherNetworkRepository,
    ) : ViewModel() {
        private val _weatherUiState = MutableStateFlow<WeatherUiState?>(null)
        val weatherUiState: StateFlow<WeatherUiState?>
            get() = _weatherUiState

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
                weatherNetworkRepository
                    .getCurrentWeather(location)
                    .catch { }
                    .collect { weather ->
                        _weatherUiState.value = weather.toUiState(location)
                    }
            }
        }

        private fun WeatherDataSource.toUiState(location: LocationEntity): WeatherUiState =
            WeatherUiState(
                locationName = location.toDisplayString,
                temperature = "${current.temp.toInt()}°",
                description =
                    current.weather
                        .firstOrNull()
                        ?.description
                        .orEmpty(),
                dateTitle = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()),
                feelsLike = "Feels like ${current.feelsLike.toInt()}°",
                hasAqi = false,
                aqiValue = "0",
                hasAlerts = !alerts.isNullOrEmpty(),
                iconRes =
                    current.weather
                        .firstOrNull()
                        ?.icon
                        .orEmpty()
                        .getIconForCondition,
            )
    }
