package com.weatheruous.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.utilities.Resource
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationList = MutableStateFlow<Resource<List<LocationEntity>>>(Resource.Loading)
    val locationList: StateFlow<Resource<List<LocationEntity>>>
        get() = _locationList

    init {
        viewModelScope.launch {
            locationRepository.getAllLocationsFromDatabase().catch {
                _locationList.value = Resource.Error(message = it.message ?: "An error occurred")
            }.collect {
                _locationList.value = Resource.Success(it)
            }
        }
    }
}
