package com.weatheruous.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.network.Resource
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.data.repositories.weather.WeatherNetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainWeatherViewModel @Inject constructor(
    private val weatherNetworkRepository: WeatherNetworkRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _weatherState = MutableStateFlow<Resource>(Resource.Loading)
    val weatherState: StateFlow<Resource>
        get() = _weatherState

    private val _locationState = MutableStateFlow(locationRepository.getDefaultLocation())
    val locationState: StateFlow<LocationEntity> = _locationState

    init {
        viewModelScope.launch {
            locationRepository.getCurrentLocationFromDatabase().collect {
                _locationState.value = it
            }
        }
        Log.d("MainWeatherViewModel", "_locationState.value: ${_locationState.value}")

        viewModelScope.launch {
            weatherNetworkRepository.getCurrentWeather(_locationState.value).collect {
                _weatherState.value = it
            }
        }
    }
}
