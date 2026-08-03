package com.warbler.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class AirQualitySource(
    @SerialName("coord")
    val coord: Coord,
    @SerialName("list")
    val list: List<Aqi>,
)
