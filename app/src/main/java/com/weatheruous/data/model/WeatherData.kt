package com.weatheruous.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.format.DateTimeFormatter

@Entity(tableName = "weather_table")
data class WeatherData(
    val city: String,
    val condition: String,
    val description: String,
    @PrimaryKey(autoGenerate = true) val id: Int,
    val hi: Double,
    val low: Double,
    val precipitation: Int,
    val pressure: Double,
    val state: String,
    val temp: Double,
    val updated: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
    val wind: Double
)
