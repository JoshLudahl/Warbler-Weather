package com.warbler.data.repositories.location

import android.util.Log
import com.warbler.data.model.location.LocationDto
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.network.locations.LocationApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationNetworkRepository
    @Inject
    constructor(
        private val locationApiService: LocationApiService,
    ) {
        fun getLocationsFromGeoService(location: String): Flow<List<LocationEntity>> =
            flow {
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

        fun reverseGeocodeLocation(
            latitude: Double,
            longitude: Double,
        ): Flow<LocationEntity?> =
            flow {
                Log.d("LocationNetworkRepository", "Performing reverse geocode for $latitude, $longitude")
                try {
                    val locations = locationApiService.reverseGeocode(latitude, longitude)
                    if (locations.isEmpty()) {
                        Log.d("LocationNetworkRepository", "No reverse geocode results, returning null.")
                        emit(null)
                    } else {
                        val location = locations.first()
                        val locationEntity = LocationDto.locationDataSourceToLocationEntity(location)
                        Log.d("LocationNetworkRepository", "Reverse geocode result: $locationEntity")
                        emit(locationEntity)
                    }
                } catch (e: Exception) {
                    Log.e("LocationNetworkRepository", "Error reverse geocoding: ${e.message}")
                    emit(null)
                }
            }.flowOn(Dispatchers.IO)
    }
