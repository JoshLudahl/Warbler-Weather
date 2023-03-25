package com.weatheruous.data.model.location

import androidx.room.Entity

@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String? = null,
    val current: Boolean = true
)
