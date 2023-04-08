package com.weatheruous.data.database.location

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.weatheruous.data.model.location.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table ORDER by updated DESC LIMIT 1")
    fun getCurrentLocation(): LocationEntity?

    @Update
    suspend fun updateCurrentLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table ORDER BY updated DESC")
    fun getAllLocations(): List<LocationEntity>?

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}
