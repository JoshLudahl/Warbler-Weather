package com.weatheruous.data.network.locations

import com.weatheruous.data.model.location.Location
import com.weatheruous.data.network.NetworkConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {
    @GET("geo/1.0/direct")
    fun getLocations(
        @Query("q") query: String,
        @Query("limit") limit: Int = NetworkConstants.CITY_SEARCH_LIMIT,
        @Query("appid") apiKey: String = NetworkConstants.WEATHER_API_KEY
    ): Call<Location>
}