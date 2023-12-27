package com.warbler.data.repositories.location

import android.util.Log
import com.warbler.data.database.location.LocationDao
import com.warbler.data.model.location.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LocationRepository
    @Inject
    constructor(
        private val locationDao: LocationDao,
    ) {
        suspend fun getCurrentLocationFromDatabase(): Flow<LocationEntity> =
            flow {
                val currentLocation = locationDao.getCurrentLocation()
                currentLocation?.let {
                    emit(it)
                } ?: emit(getDefaultLocation())
            }.flowOn(Dispatchers.Main)

        fun getAllLocationsFromDatabase(): Flow<List<LocationEntity>> = locationDao.getAllLocations()

        suspend fun saveLocationToDatabaseAndSetAsCurrent(location: LocationEntity) {
            updateToCurrentLocation(location)
            locationDao.insertLocation(location)
        }

        suspend fun updateToCurrentLocation(location: LocationEntity) {
            val updated = location.copy(updated = System.currentTimeMillis())
            locationDao.updateCurrentLocation(updated)
        }

        suspend fun deleteLocation(location: LocationEntity) {
            Log.d("LocationRepository", "Deleting location: $location")
            locationDao.deleteLocation(location)
        }

        fun getDefaultLocation(): LocationEntity {
            return LocationEntity(
                country = "US",
                lat = 45.5152,
                lon = -122.6793,
                name = "Portland",
                state = "Oregon",
                current = true,
            )
        }
    }
