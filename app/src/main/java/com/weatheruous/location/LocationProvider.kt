package com.weatheruous.location

interface LocationProvider {

    suspend fun hasLocationChanged(): Boolean
    suspend fun getLastLocation(): String

}