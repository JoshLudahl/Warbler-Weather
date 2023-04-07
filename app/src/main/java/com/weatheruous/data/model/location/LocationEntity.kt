package com.weatheruous.data.model.location

import androidx.room.Entity

@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    val country: String,
    val current: Boolean = true,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String? = null

) {
    fun toDisplayString(): String {
        return if (state != null) {
            "$name, $state $country"
        } else {
            "$name, $country"
        }
    }
}
