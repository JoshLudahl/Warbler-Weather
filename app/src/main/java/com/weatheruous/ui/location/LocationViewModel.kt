package com.weatheruous.ui.location

import androidx.lifecycle.ViewModel
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.utilities.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _locationList = MutableStateFlow<Resource<List<LocationEntity>>>(Resource.Loading)
    val locationList: StateFlow<Resource<List<LocationEntity>>>
        get() = _locationList
}
