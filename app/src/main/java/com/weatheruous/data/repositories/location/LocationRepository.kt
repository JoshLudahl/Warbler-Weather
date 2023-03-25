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
    fun getCurrentLocationFromDatabase(): Flow<LocationEntity> {
        return locationDao.getCurrentLocation()?.flowOn(Dispatchers.IO) ?: flow {
            emit(getDefaultLocation())
        }
    }

    fun saveLocationToDatabaseAndSetAsCurrent(location: LocationEntity) {
        locationDao.updateCurrentLocation()
        locationDao.insertLocation(location)
    }

    fun getDefaultLocation(): LocationEntity {
        return LocationEntity(
            lat = 45.5152,
            lon = -122.6793,
            name = "Portland",
            state = "Oregon",
            current = true
        )
    }
}
