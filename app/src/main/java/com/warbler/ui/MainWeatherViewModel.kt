package com.warbler.ui

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.model.weather.AirQualitySource
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.repositories.location.LocationRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.ui.settings.Accumulation
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.DataPref
import com.warbler.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainWeatherViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val locationRepository: LocationRepository,
        private val weatherNetworkRepository: WeatherNetworkRepository,
    ) : ViewModel(),
        LifecycleObserver {
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

        private val _accumulationUnit = MutableStateFlow(Accumulation.MILLIMETERS_PER_HOUR)
        val accumulationUnit: StateFlow<Accumulation>
            get() = _accumulationUnit

        private val _weatherObject = MutableStateFlow<WeatherDataSource?>(null)
        val weatherObject: StateFlow<WeatherDataSource?>
            get() = _weatherObject

        private val _errorView = MutableStateFlow(false)
        val errorView: StateFlow<Boolean>
            get() = _errorView

        private val _isDisabled = MutableStateFlow(false)
        val isDisabled: StateFlow<Boolean>
            get() = _isDisabled

        private val aqi = MutableStateFlow<Resource<AirQualitySource>>(Resource.Loading)

        private val _hasAqi = MutableStateFlow(false)
        val hasAqi: StateFlow<Boolean>
            get() = _hasAqi

        private val _aqiValue = MutableStateFlow("0")
        val aqiValue: StateFlow<String>
            get() = _aqiValue

        init {
            viewModelScope.launch {
                locationRepository
                    .getCurrentLocationFromDatabase()
                    .catch { e ->
                        Log.d("MainWeatherViewModel", "Caught error getting location: ${e.message}")
                        _locationState.value =
                            Resource.Error(
                                message = e.message ?: "An error occurred",
                            )
                    }.collect {
                        Log.d("MainWeatherViewModel", "Setting location to $it")
                        _locationState.value = Resource.Success(it)
                        _currentLocation.value = it
                        getAqi()
                    }
            }

            viewModelScope.launch {
                DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore).collect {
                    _temperatureUnit.value =
                        when (it) {
                            0 -> Temperature.CELSIUS
                            1 -> Temperature.FAHRENHEIT
                            2 -> Temperature.KELVIN
                            else -> Temperature.FAHRENHEIT
                        }
                }
            }

            viewModelScope.launch {
                DataPref.readIntDataStoreFlow(DataPref.SPEED_UNIT, dataStore).collect {
                    _speedUnit.value =
                        when (it) {
                            0 -> Speed.MPH
                            1 -> Speed.MPS
                            2 -> Speed.KPH
                            else -> Speed.MPH
                        }
                }
            }

            viewModelScope.launch {
                DataPref.readIntDataStoreFlow(DataPref.ACCUMULATION_UNIT, dataStore).collect {
                    _accumulationUnit.value =
                        when (it) {
                            0 -> Accumulation.INCHES_PER_HOUR
                            1 -> Accumulation.MILLIMETERS_PER_HOUR
                            else -> Accumulation.MILLIMETERS_PER_HOUR
                        }
                }
            }

            viewModelScope.launch {
                _isDisabled.collect()
            }
        }

        private fun getAqi() {
            viewModelScope.launch {
                weatherNetworkRepository.getCurrentAqi(currentLocation.value).collect { resource ->
                    when (resource) {
                        Resource.Loading -> {
                            Log.d("MainWeatherViewModel", "Loading AQI.")
                            _hasAqi.value = false
                        }

                        is Resource.Success -> {
                            resource.data.let {
                                Log.d(
                                    "MainWeatherViewModel",
                                    "Updating ViewModel with AQI: ${it.list[0].main.aqi}",
                                )
                                aqi.value = Resource.Success(it)
                                _aqiValue.value =
                                    it.list[0]
                                        .main.aqi
                                        .toString()
                                _hasAqi.value = true
                            }
                        }

                        is Resource.Error -> {
                            Log.d("MainWeatherViewModel", "AQI failed.")
                            _hasAqi.value = false
                        }
                    }
                }
            }
        }

        fun updateWeatherData() {
            Log.d("MainWeatherViewModel", "Setting to Loading state.")
            _weatherState.value = Resource.Loading
            when (val location = _locationState.value) {
                is Resource.Success -> {
                    Log.d("MainWeatherViewModel", "Setting to Success state")
                    viewModelScope.launch {
                        handleGetWeather(location.data)
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
            Log.i("Log", "isDisabled Before: ${isDisabled.value}")
            _isDisabled.value = true
            Log.i("Log", "isDisabled After: ${isDisabled.value}")
            val currentLocation: LocationEntity = locationEntity
            weatherNetworkRepository
                .getCurrentWeather(currentLocation)
                .catch { e ->
                    _weatherState.value =
                        Resource.Error(
                            message = e.message ?: "An error occurred.",
                        )
                    _errorView.value = true
                    Log.d("MainWeatherViewModel", "error view: ${_errorView.value} Caught error3e: $e")
                }.collect {
                    Log.d(
                        "MainWeatherViewModel",
                        "Updating ViewModel with data: $it",
                    )

                    _errorView.value = false
                    _weatherState.value = Resource.Success(it)
                    _weatherObject.value = it
                }
            _isDisabled.value = false
        }
    }
