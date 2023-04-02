package com.weatheruous.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.model.weather.Conversion
import com.weatheruous.data.model.weather.WeatherDataSource
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.data.repositories.weather.WeatherNetworkRepository
import com.weatheruous.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class MainWeatherViewModel @Inject constructor(
    private val weatherNetworkRepository: WeatherNetworkRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _weatherState = MutableStateFlow<Resource<WeatherDataSource>>(Resource.Loading)
    val weatherState: StateFlow<Resource<WeatherDataSource>>
        get() = _weatherState

    private val _locationState = MutableStateFlow<Resource<LocationEntity>>(Resource.Loading)
    val locationState: StateFlow<Resource<LocationEntity>> = _locationState

    private val _dateTitle =
        MutableStateFlow(Conversion.getFormattedDateFromTimeStamp(Instant.now()))
    val dateTitle: StateFlow<String> = _dateTitle

    init {
        viewModelScope.launch {
            locationRepository.getCurrentLocationFromDatabase().catch { e ->
                _locationState.value = Resource.Error(
                    message = e.message ?: "An error occurred"
                )
            }.collect {
                _locationState.value = Resource.Success(it)
            }
        }
    }
    fun updateWeatherData() {
        _weatherState.value = Resource.Loading
        when (val location = _locationState.value) {
            is Resource.Success<*> -> {
                viewModelScope.launch {
                    val currentLocation: LocationEntity = location.data as LocationEntity
                    weatherNetworkRepository.getCurrentWeather(currentLocation)
                        .catch { e ->
                            _weatherState.value = Resource.Error(
                                message = e.message ?: "An error occurred"
                            )

                            Log.d(
                                "MainWeatherViewModel",
                                "Error getting weather data: ${e.message}"
                            )
                        }
                        .collect {
                            _weatherState.value = Resource.Success(it)
                        }
                }
            }
            is Resource.Error -> {
                Log.d("MainWeatherViewModel", "Error getting location.")
            }
            is Resource.Loading -> {
                Log.d("MainWeatherViewModel", "Loading Weather")
            }
        }
    }
}
