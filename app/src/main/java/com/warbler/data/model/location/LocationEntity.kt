package com.warbler.data.model.location

import androidx.annotation.Keep
import androidx.room.Entity

@Keep
@Entity(tableName = "location_table", primaryKeys = ["lat", "lon"])
data class LocationEntity(
    val country: String,
    val current: Boolean = true,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String? = null,
    val updated: Long = System.currentTimeMillis(),
) {
    val toDisplayString
        get() =
            if (state != null) {
                "$name, $state $country"
            } else {
                "$name, $country"
            }
}
