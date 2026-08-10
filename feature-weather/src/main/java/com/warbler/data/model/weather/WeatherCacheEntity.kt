package com.warbler.data.model.weather

import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "weather_cache", primaryKeys = ["lat", "lon"])
data class WeatherCacheEntity(
    val lat: Double,
    val lon: Double,
    val json: String,
    val lastUpdated: Long = System.currentTimeMillis(),
)
