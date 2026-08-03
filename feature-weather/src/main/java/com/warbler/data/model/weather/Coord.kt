package com.warbler.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Coord(
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
)
