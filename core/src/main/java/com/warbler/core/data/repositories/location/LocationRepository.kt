package com.warbler.core.data.repositories.location

import android.util.Log
import com.warbler.core.data.database.location.LocationDao
import com.warbler.core.model.location.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationRepository
    @Inject
    constructor(
        private val locationDao: LocationDao,
    ) {
        fun getCurrentLocationFromDatabase(): Flow<LocationEntity> =
            locationDao
                .getCurrentLocation()
                .map { currentLocation ->
                    currentLocation ?: getDefaultLocation()
                }.flowOn(Dispatchers.IO)

        fun getAllLocationsFromDatabase(): Flow<List<LocationEntity>> {
            Log.d("LocationRepository", "Fetching all locations from database")
            return locationDao.getAllLocations()
        }

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

        fun getDefaultLocation(): LocationEntity =
            LocationEntity(
                country = "US",
                lat = 45.5152,
                lon = -122.6793,
                name = "Portland",
                state = "Oregon",
                current = true,
            )
    }
