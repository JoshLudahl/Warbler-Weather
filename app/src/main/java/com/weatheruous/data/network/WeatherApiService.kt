package com.weatheruous.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.weatheruous.BuildConfig
import com.weatheruous.data.model.WeatherData
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = BuildConfig.WEATHER_BASE_URL

private val contentType = "application/json".toMediaType()

@OptIn(ExperimentalSerializationApi::class)
private val retrofit =
    Retrofit.Builder().addConverterFactory(Json.asConverterFactory(contentType = contentType))
        .baseUrl(BASE_URL).build()

interface WeatherApiService {
    @GET("/data/3.0/onecall?lat={lat}&lon={lon}&appid={apiKey}")
    fun getWeather(
        @Path("apiKey") apiKey: String,
        @Path("lat") lat: Double,
        @Path("lon") lon: Double
    ): Call<WeatherData>
}

object WeatherAPI {
    val weatherService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
