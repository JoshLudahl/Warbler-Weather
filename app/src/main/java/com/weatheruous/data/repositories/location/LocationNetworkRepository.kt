package com.weatheruous.data.repositories.location

import android.util.Log
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
        Log.d("LocationNetworkRepository", "Performing search for $location")
        try {
            val locations = locationApiService.getLocations(location)
            val locationsMapped = ArrayList<LocationEntity>()
            if (locations.isEmpty()) {
                Log.d("LocationNetworkRepository", "No results, returning empty list.")
                emit(arrayListOf<LocationEntity>())
            }
            Log.d("LocationNetworkRepository", "Filtering results... $locations")
            locations.forEach {
                it.let { locationsMapped.add(LocationDto.locationDataSourceToLocationEntity(it)) }
            }
            emit(locationsMapped)
        } catch (e: Exception) {
            Log.e("LocationNetworkRepository", "Error getting list: ${e.message}")
            emit(arrayListOf<LocationEntity>())
        }
    }.flowOn(Dispatchers.IO)
}
