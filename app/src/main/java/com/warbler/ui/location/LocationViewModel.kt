package com.warbler.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.repositories.location.LocationNetworkRepository
import com.warbler.data.repositories.location.LocationRepository
import com.warbler.utilities.LocationService
import com.warbler.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel
    @Inject
    constructor(
        private val locationRepository: LocationRepository,
        private val locationNetworkRepository: LocationNetworkRepository,
        private val locationService: LocationService,
    ) : ViewModel() {
        private val _locationList = MutableStateFlow<Resource<List<LocationEntity>>>(Resource.Loading)
        val locationList: StateFlow<Resource<List<LocationEntity>>>
            get() = _locationList

        private val _locationSearchList =
            MutableStateFlow<Resource<List<LocationEntity>>>(
                Resource.Loading,
            )
        val locationSearchList: StateFlow<Resource<List<LocationEntity>>>
            get() = _locationSearchList

        private val _currentLocationSaved = MutableStateFlow(false)
        val currentLocationSaved: StateFlow<Boolean>
            get() = _currentLocationSaved

        private val _isLoadingCurrentLocation = MutableStateFlow(false)
        val isLoadingCurrentLocation: StateFlow<Boolean>
            get() = _isLoadingCurrentLocation

        init {
            viewModelScope.launch {
                locationRepository
                    .getAllLocationsFromDatabase()
                    .catch {
                        Log.d("LocationViewModel", "Error: ${it.message}")
                        _locationList.value = Resource.Error(message = it.message)
                    }.collect {
                        Log.d("LocationViewModel", "Success: $it")
                        _locationList.value = Resource.Success(it)
                    }
            }
        }

        fun saveToDatabase(location: LocationEntity) {
            viewModelScope.launch {
                locationRepository.saveLocationToDatabaseAndSetAsCurrent(location)
            }
        }

        fun deleteFromDatabase(location: LocationEntity) {
            Log.d("LocationViewModel", "deleteFromDatabase launching coroutine...")
            viewModelScope.launch {
                locationRepository.deleteLocation(location)
            }
        }

        fun updateCurrentLocation(location: LocationEntity) {
            viewModelScope.launch(Dispatchers.IO) {
                locationRepository.updateToCurrentLocation(location)
            }
        }

        fun searchForLocation(query: String) {
            Log.d("LocationViewModel", "searchForLocation launching coroutine...")
            viewModelScope.launch {
                Log.d("LocationViewModel", "searchForLocation Attempting to search for: $query")
                locationNetworkRepository
                    .getLocationsFromGeoService(query)
                    .catch { error ->
                        Log.d("LocationViewModel", "searchForLocation error: ${error.message}")
                    }.collect {
                        Log.i("LocationViewModel", "searchForLocation success: ${it.size}")
                        _locationSearchList.value = Resource.Success(it)
                    }
            }
        }

        fun getCurrentLocationAndSave() {
            Log.d("LocationViewModel", "getCurrentLocationAndSave starting...")
            viewModelScope.launch {
                try {
                    _isLoadingCurrentLocation.value = true

                    // Check permissions first
                    if (!locationService.hasLocationPermission()) {
                        Log.e("LocationViewModel", "Location permission not granted")
                        _isLoadingCurrentLocation.value = false
                        return@launch
                    }

                    // Get current location
                    val location = locationService.getCurrentLocation()
                    if (location == null) {
                        Log.e("LocationViewModel", "Failed to get current location")
                        _isLoadingCurrentLocation.value = false
                        return@launch
                    }

                    Log.d("LocationViewModel", "Got location: ${location.latitude}, ${location.longitude}")

                    // Reverse geocode the location
                    locationNetworkRepository
                        .reverseGeocodeLocation(location.latitude, location.longitude)
                        .catch { error ->
                            Log.e("LocationViewModel", "Reverse geocode error: ${error.message}")
                            _isLoadingCurrentLocation.value = false
                        }.collect { locationEntity ->
                            if (locationEntity != null) {
                                Log.d("LocationViewModel", "Reverse geocode success: $locationEntity")
                                // Save to database and set as current
                                locationRepository.saveLocationToDatabaseAndSetAsCurrent(locationEntity)
                                Log.d("LocationViewModel", "Location saved to database")
                                // Emit success state
                                _currentLocationSaved.value = true
                                _isLoadingCurrentLocation.value = false
                            } else {
                                Log.e("LocationViewModel", "Reverse geocode returned null")
                                _isLoadingCurrentLocation.value = false
                            }
                        }
                } catch (e: Exception) {
                    Log.e("LocationViewModel", "Error in getCurrentLocationAndSave: ${e.message}")
                    _isLoadingCurrentLocation.value = false
                }
            }
        }
    }
