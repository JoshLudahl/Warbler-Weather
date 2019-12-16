package com.weatheruous.Location

interface LocationProvider {

    suspend fun hasLocationChanged(): Boolean
    suspend fun getLastLocation(): String

}