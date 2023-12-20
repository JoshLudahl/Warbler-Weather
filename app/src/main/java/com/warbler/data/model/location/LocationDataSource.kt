package com.warbler.data.model.location
import androidx.annotation.Keep
import kotlinx.serialization.SerialName

@Keep

data class LocationDataSource(
    @SerialName("country")
    val country: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: String? = null
)
