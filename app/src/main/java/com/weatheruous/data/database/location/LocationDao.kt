package com.weatheruous.data.database.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.weatheruous.data.model.location.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table where current = 1")
    fun getCurrentLocation(): LocationEntity?

    @Query("UPDATE location_table SET current = 0 where current = 1")
    fun updateCurrentLocation()

    @Query("SELECT * FROM location_table")
    fun getAllLocations(): List<LocationEntity>?
}
