package com.warbler.data.database.weather

import androidx.room.Database
import androidx.room.RoomDatabase
import com.warbler.data.database.location.LocationDao
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.model.weather.WeatherDataEntity

@Database(
    entities = [
        LocationEntity::class,
        WeatherDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDatabaseDao

    abstract fun locationDao(): LocationDao
}
