package com.weatheruous.data.repositories.location

import com.weatheruous.data.model.location.LocationDto
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.network.locations.LocationApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LocationNetworkRepository @Inject constructor(
    private val locationApiService: LocationApiService

) {
    fun getLocationsFromGeoService(location: String): Flow<List<LocationEntity>> = flow {
        val locations = locationApiService.getLocations(location)
        val locationsMapped = ArrayList<LocationEntity>()
        if (locations.isEmpty()) {
            emit(arrayListOf())
        }
        locations.forEach {
            it?.let { locationsMapped.add(LocationDto.locationDataSourceToLocationEntity(it)) }
        }
        emit(locationsMapped)
    }.flowOn(Dispatchers.IO)
}
