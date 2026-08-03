package com.warbler.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Aqi(
    @SerialName("components")
    val components: Components,
    @SerialName("dt")
    val dt: Long,
    @SerialName("main")
    val main: Main,
)
