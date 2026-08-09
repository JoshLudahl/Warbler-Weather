package com.warbler.feature.location.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.data.repositories.location.LocationNetworkRepository
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.utilities.LocationService
import com.warbler.core.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class LocationViewModel
    @OptIn(FlowPreview::class)
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
                Resource.Success(emptyList()),
            )
        val locationSearchList: StateFlow<Resource<List<LocationEntity>>>
            get() = _locationSearchList

        private val _currentLocationSaved = MutableStateFlow(false)
        val currentLocationSaved: StateFlow<Boolean>
            get() = _currentLocationSaved

        private val _isLoadingCurrentLocation = MutableStateFlow(false)
        val isLoadingCurrentLocation: StateFlow<Boolean>
            get() = _isLoadingCurrentLocation

        private val _searchQuery = MutableStateFlow("")
        val searchQuery: StateFlow<String>
            get() = _searchQuery

        private val _isSearchBarActive = MutableStateFlow(false)
        val isSearchBarActive: StateFlow<Boolean>
            get() = _isSearchBarActive

        private val _errorMessage = MutableStateFlow<String?>(null)
        val errorMessage: StateFlow<String?>
            get() = _errorMessage

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

            viewModelScope.launch {
                _searchQuery
                    .debounce(500.milliseconds)
                    .distinctUntilChanged()
                    .filter { it.length >= 3 }
                    .collect { query ->
                        searchForLocation(query)
                    }
            }
        }

        fun saveToDatabase(location: LocationEntity) {
            Log.d("LocationViewModel", "saveToDatabase: ${location.name}")
            viewModelScope.launch {
                locationRepository.saveLocationToDatabaseAndSetAsCurrent(location)
                Log.d("LocationViewModel", "saveToDatabase: finished")
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

        fun onSearchQueryChange(query: String) {
            _searchQuery.value = query
            if (query.length < 3) {
                _locationSearchList.value = Resource.Success(emptyList())
            }
        }

        fun onSearchBarActiveChange(active: Boolean) {
            _isSearchBarActive.value = active
        }

        fun resetCurrentLocationSaved() {
            _currentLocationSaved.value = false
        }

        fun searchForLocation(query: String) {
            Log.d("LocationViewModel", "searchForLocation launching coroutine...")
            _locationSearchList.value = Resource.Loading
            viewModelScope.launch {
                Log.d("LocationViewModel", "searchForLocation Attempting to search for: $query")
                locationNetworkRepository
                    .getLocationsFromGeoService(query)
                    .catch { error ->
                        Log.d("LocationViewModel", "searchForLocation error: ${error.message}")
                        _locationSearchList.value = Resource.Error(message = error.message)
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
                    _errorMessage.value = null
                    _currentLocationSaved.value = false

                    // Check permissions first
                    if (!locationService.hasLocationPermission()) {
                        Log.e("LocationViewModel", "Location permission not granted")
                        _errorMessage.value = "Location permission not granted"
                        _isLoadingCurrentLocation.value = false
                        return@launch
                    }

                    // Get current location
                    val location = locationService.getCurrentLocation()
                    Log.d("LocationViewModel", "Got location: $location")
                    if (location == null) {
                        Log.e("LocationViewModel", "Failed to get current location")
                        _errorMessage.value = "Failed to get current location"
                        _isLoadingCurrentLocation.value = false
                        return@launch
                    }

                    Log.d("LocationViewModel", "Got location: ${location.latitude}, ${location.longitude}")

                    // Reverse geocode the location
                    locationNetworkRepository
                        .reverseGeocodeLocation(location.latitude, location.longitude)
                        .catch { error ->
                            Log.e("LocationViewModel", "Reverse geocode error: ${error.message}")
                            _errorMessage.value = "Error resolving location name"
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
                                _errorMessage.value = "Could not find your location name"
                                _isLoadingCurrentLocation.value = false
                            }
                        }
                } catch (e: Exception) {
                    Log.e("LocationViewModel", "Error in getCurrentLocationAndSave: ${e.message}")
                    _errorMessage.value = "An unexpected error occurred"
                    _isLoadingCurrentLocation.value = false
                }
            }
        }
    }
