package com.weatheruous.data.database.weather

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weatheruous.data.model.WeatherData

@Database(
    entities = [
        WeatherData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDatabaseDao
}
