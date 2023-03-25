package com.weatheruous.data.network.weather

import com.weatheruous.data.model.weather.WeatherDataSource
import com.weatheruous.data.network.NetworkConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/3.0/onecall")
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = NetworkConstants.WEATHER_API_KEY
    ): Call<WeatherDataSource>
}
