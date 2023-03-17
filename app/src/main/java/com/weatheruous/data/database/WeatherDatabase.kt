package com.weatheruous.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatheruous.data.model.WeatherData

@Database(
    entities = [
        WeatherData::class
    ],
    version = 6,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDatabaseDao
}
