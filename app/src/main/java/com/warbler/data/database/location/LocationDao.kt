package com.warbler.data.database.location

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.warbler.data.model.location.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table ORDER by updated DESC LIMIT 1")
    fun getCurrentLocation(): LocationEntity?

    @Update
    suspend fun updateCurrentLocation(location: LocationEntity)

    @Query("SELECT * FROM location_table ORDER BY updated DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Delete
    suspend fun deleteLocation(location: LocationEntity)
}
