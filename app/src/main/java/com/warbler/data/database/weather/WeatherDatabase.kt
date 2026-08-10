package com.warbler.data.database.weather

import androidx.room.Database
import androidx.room.RoomDatabase
import com.warbler.core.data.database.location.LocationDao
import com.warbler.core.model.location.LocationEntity
import com.warbler.data.model.weather.WeatherCacheEntity
import com.warbler.data.model.weather.WeatherDataEntity

@Database(
    entities = [
        LocationEntity::class,
        WeatherDataEntity::class,
        WeatherCacheEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDatabaseDao

    abstract fun locationDao(): LocationDao
}
