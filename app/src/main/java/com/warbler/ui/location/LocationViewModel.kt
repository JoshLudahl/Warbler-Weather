package com.warbler.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.repositories.location.LocationNetworkRepository
import com.warbler.data.repositories.location.LocationRepository
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

        init {
            viewModelScope.launch {
                locationRepository.getAllLocationsFromDatabase().catch {
                    Log.d("LocationViewModel", "Error: ${it.message}")
                    _locationList.value = Resource.Error(message = it.message)
                }.collect {
                    Log.d("LocationViewModel", "Success: $it")
                    _locationList.value = Resource.Success(it)
                }
            }
        }

        suspend fun saveToDatabase(location: LocationEntity) {
            viewModelScope.launch {
                locationRepository.saveLocationToDatabaseAndSetAsCurrent(location)
            }
        }

        suspend fun deleteFromDatabase(location: LocationEntity) {
            Log.d("LocationViewModel", "deleteFromDatabase launching coroutine...")
            viewModelScope.launch {
                locationRepository.deleteLocation(location)
            }
        }

        suspend fun updateCurrentLocation(location: LocationEntity) {
            viewModelScope.launch(Dispatchers.IO) {
                locationRepository.updateToCurrentLocation(location)
            }
        }

        fun searchForLocation(query: String) {
            Log.d("LocationViewModel", "searchForLocation launching coroutine...")
            viewModelScope.launch {
                Log.d("LocationViewModel", "searchForLocation Attempting to search for: $query")
                locationNetworkRepository.getLocationsFromGeoService(query).catch { error ->
                    Log.d("LocationViewModel", "searchForLocation error: ${error.message}")
                }.collect {
                    Log.i("LocationViewModel", "searchForLocation success: ${it.size}")
                    _locationSearchList.value = Resource.Success(it)
                }
            }
        }
    }
