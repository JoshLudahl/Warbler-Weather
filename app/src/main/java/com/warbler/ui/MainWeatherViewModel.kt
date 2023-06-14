package com.warbler.ui

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.network.ConnectionHandler
import com.warbler.data.repositories.location.LocationRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.DataPref
import com.warbler.utilities.Resource
import com.warbler.utilities.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
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
        MutableStateFlow(Conversion.currentDate)
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
                Log.d("MainWeatherViewModel", "Caught error getting location: ${e.message}")
                _locationState.value = Resource.Error(
                    message = e.message ?: "An error occurred"
                )
            }.collect {
                Log.d("MainWeatherViewModel", "Setting location to $it")
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

    fun updateWeatherData(context: Context) {
        Log.d("MainWeatherViewModel", "Setting to Loading state.")
        _weatherState.value = Resource.Loading
        when (val location = _locationState.value) {
            is Resource.Success -> {
                Log.d("MainWeatherViewModel", "Setting to Success state")
                viewModelScope.launch {
                    if (ConnectionHandler.isOnline(context)) {
                        handleGetWeather(location.data)
                    } else {
                        Log.d("MainWeatherViewModel", "No Internet")
                        _weatherState.value = Resource.Error(message = "No Internet Available")
                        context.showToast("No internet connection.")
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

    private suspend fun handleGetWeather(locationEntity: LocationEntity) {
        val currentLocation: LocationEntity = locationEntity as LocationEntity
        weatherNetworkRepository.getCurrentWeather(currentLocation)
            .catch { e ->
                _weatherState.value = Resource.Error(
                    message = e.message ?: "An error occurred."
                )
                Log.d("MainWeatherViewModel", "Caught error: $e")
            }
            .collect {
                Log.d(
                    "MainWeatherViewModel",
                    "Updating ViewModel with data: $it"
                )

                _weatherState.value = Resource.Success(it)
                _weatherObject.value = it
            }
    }
}
