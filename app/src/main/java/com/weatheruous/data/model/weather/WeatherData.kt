package com.weatheruous.data.model.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.format.DateTimeFormatter

@Entity(tableName = "weather_table", primaryKeys = ["lat", "lon"])
data class WeatherData(
    val city: String,
    val condition: String,
    val description: String,
    val hi: Double,
    val lat: Double,
    val lon: Double,
    val low: Double,
    val pressure: Int,
    val temp: Double,
    val updated: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
    val wind: Double
)
