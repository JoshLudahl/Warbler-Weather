package com.weatheruous.ui

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.model.weather.Conversion
import com.weatheruous.data.model.weather.WeatherDataSource
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.data.repositories.weather.WeatherNetworkRepository
import com.weatheruous.ui.settings.Speed
import com.weatheruous.ui.settings.Temperature
import com.weatheruous.utilities.DataPref
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
    private val dataStore: DataStore<Preferences>,
    private val locationRepository: LocationRepository,
    private val weatherNetworkRepository: WeatherNetworkRepository
) : ViewModel() {

    private val _currentLocation = MutableStateFlow(locationRepository.getDefaultLocation())
    val currentLocation: StateFlow<LocationEntity>
        get() = _currentLocation

    private val _weatherState = MutableStateFlow<Resource<WeatherDataSource>>(Resource.Loading)
    val weatherState: StateFlow<Resource<WeatherDataSource>>
        get() = _weatherState

    private val _locationState = MutableStateFlow<Resource<LocationEntity>>(Resource.Loading)
    val locationState: StateFlow<Resource<LocationEntity>>
        get() = _locationState

    private val _dateTitle =
        MutableStateFlow(Conversion.getFormattedDateFromTimeStamp(Instant.now()))
    val dateTitle: StateFlow<String>
        get() = _dateTitle

    private val _temperatureUnit = MutableStateFlow(Temperature.CELSIUS)
    val temperatureUnit: StateFlow<Temperature>
        get() = _temperatureUnit

    private val _speedUnit = MutableStateFlow(Speed.MPH)
    val speedUnit: StateFlow<Speed>
        get() = _speedUnit

    private val _weatherObject = MutableStateFlow<WeatherDataSource?>(null)
    val weatherObject: StateFlow<WeatherDataSource?>
        get() = _weatherObject

    init {
        viewModelScope.launch {
            locationRepository.getCurrentLocationFromDatabase().catch { e ->
                _locationState.value = Resource.Error(
                    message = e.message ?: "An error occurred"
                )
            }.collect {
                _locationState.value = Resource.Success(it)
                _currentLocation.value = it
            }
        }

        viewModelScope.launch {
            DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore).collect {
                _temperatureUnit.value = when (it) {
                    0 -> Temperature.CELSIUS
                    1 -> Temperature.FAHRENHEIT
                    2 -> Temperature.KELVIN
                    else -> Temperature.FAHRENHEIT
                }
            }
            updateTemperatureUnitToReflectUnitChange()
        }

        viewModelScope.launch {
            DataPref.readIntDataStoreFlow(DataPref.SPEED_UNIT, dataStore).collect {
                _speedUnit.value = when (it) {
                    0 -> Speed.MPH
                    1 -> Speed.KPH
                    else -> Speed.MPH
                }
            }
            updateSpeedUnitToReflectUnitChange()
        }
    }

    private fun updateTemperatureUnitToReflectUnitChange() {
        TODO("Not yet implemented")
    }

    private fun updateSpeedUnitToReflectUnitChange() {
        TODO("Not yet implemented")
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
                                message = e.message ?: "An error occurred."
                            )
                        }
                        .collect {
                            _weatherState.value = Resource.Success(it)
                            _weatherObject.value = it
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
