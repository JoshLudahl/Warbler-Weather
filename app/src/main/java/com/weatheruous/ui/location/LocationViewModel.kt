package com.weatheruous.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.repositories.location.LocationNetworkRepository
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val locationNetworkRepository: LocationNetworkRepository
) : ViewModel() {

    private val _locationList = MutableStateFlow<Resource<List<LocationEntity>>>(Resource.Loading)
    val locationList: StateFlow<Resource<List<LocationEntity>>>
        get() = _locationList

    private val _locationSearchList = MutableStateFlow<Resource<List<LocationEntity>>>(
        Resource.Loading
    )
    val locationSearchList: StateFlow<Resource<List<LocationEntity>>>
        get() = _locationSearchList

    init {
        viewModelScope.launch {
            locationRepository.getAllLocationsFromDatabase().catch {
                _locationList.value = Resource.Error(message = it.message)
            }.collect {
                _locationList.value = Resource.Success(it)
            }
        }
    }

    fun saveToDatabase(location: LocationEntity) {
        viewModelScope.launch {
            locationRepository.saveLocationToDatabaseAndSetAsCurrent(location)
        }
    }

    fun searchForLocation(query: String) {
        Log.d("LocationViewModel", "searchForLocation launching coroutine...")
        viewModelScope.launch {
            Log.d("LocationViewModel", "searchForLocation Attempting to search for: $query")
            locationNetworkRepository.getLocationsFromGeoService(query).catch { error ->
                Log.d("LocationViewModel", "searchForLocation error: ${error.message}")
            }.collect {
                Log.i("LocationViewModel", "searchForLocation success: $it")
                _locationSearchList.value = Resource.Success(it)
            }
        }
    }
}
