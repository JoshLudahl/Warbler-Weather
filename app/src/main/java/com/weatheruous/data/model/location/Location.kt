package com.weatheruous.data.model.location
import androidx.annotation.Keep

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

class Location : ArrayList<LocationItem>()

@Keep
@Serializable
data class LocationItem(
    @SerialName("country")
    val country: String,
    @SerialName("lat")
    val lat: Double,
    @SerialName("local_names")
    val localNames: LocalNames?,
    @SerialName("lon")
    val lon: Double,
    @SerialName("name")
    val name: String,
    @SerialName("state")
    val state: String
)

@Keep
@Serializable
data class LocalNames(
    val en: String
)
