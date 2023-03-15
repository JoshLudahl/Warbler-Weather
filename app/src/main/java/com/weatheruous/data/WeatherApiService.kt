package com.weatheruous.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.weatheruous.model.WeatherData
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

//  The URLS will be attached to a database and only the location will be used to obtain the correct links
private const val BASE_URL = "https://api.weather.gov"
private const val FORECAST_HOURLY_URL = "/gridpoints/PQR/107,99/forecast/hourly"
private const val FORCAST_URL = "/gridpoints/PQR/107,99/forecast"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {

    @GET
    fun setupWeatherModel(): Call<WeatherData>

    @GET(FORECAST_HOURLY_URL)
    fun getCurrentWeather(): Call<WeatherData>

    @GET(FORCAST_URL)
    fun getWeatherForecast(): Call<WeatherData>

    @GET(BASE_URL)
    fun getWeatherApiStatus(): Call<String>
}

object WeatherAPI {
    val retrofitService: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
