package com.weatheruous.data.repositories.location

import com.weatheruous.data.database.location.LocationDao
import com.weatheruous.data.model.location.LocationEntity
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LocationRepository @Inject constructor(
    private val locationDao: LocationDao
) {
    fun getCurrentLocationFromDatabase(): Flow<LocationEntity> = flow {
        val currentLocation = locationDao.getCurrentLocation()
        currentLocation?.let {
            emit(it)
        } ?: emit(getDefaultLocation())
    }.flowOn(Dispatchers.IO)

    fun getAllLocationsFromDatabase(): Flow<List<LocationEntity>> = flow {
        val allLocations = locationDao.getAllLocations()
        allLocations?.let {
            emit(it)
        } ?: emit(emptyList())
    }.flowOn(Dispatchers.IO)

    fun saveLocationToDatabaseAndSetAsCurrent(location: LocationEntity) {
        locationDao.updateCurrentLocation()
        locationDao.insertLocation(location)
    }

    fun getDefaultLocation(): LocationEntity {
        return LocationEntity(
            country = "US",
            lat = 45.5152,
            lon = -122.6793,
            name = "Portland",
            state = "Oregon",
            current = true
        )
    }
}
