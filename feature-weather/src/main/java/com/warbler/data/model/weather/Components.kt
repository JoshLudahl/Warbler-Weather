package com.warbler.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Components(
    @SerialName("co")
    val co: Double,
    @SerialName("nh3")
    val nh3: Double,
    @SerialName("no")
    val no: Double,
    @SerialName("no2")
    val no2: Double,
    @SerialName("o3")
    val o3: Double,
    @SerialName("pm10")
    val pm10: Double,
    @SerialName("pm2_5")
    val pm25: Double,
    @SerialName("so2")
    val so2: Double,
)
