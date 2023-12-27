package com.warbler.data.network.weather

import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = NetworkConstants.WEATHER_API_KEY,
    ): WeatherDataSource
}
