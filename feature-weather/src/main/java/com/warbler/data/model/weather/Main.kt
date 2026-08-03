package com.warbler.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Main(
    @SerialName("aqi")
    val aqi: Int,
)
