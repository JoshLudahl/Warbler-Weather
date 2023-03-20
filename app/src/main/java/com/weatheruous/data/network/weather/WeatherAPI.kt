package com.weatheruous.data.network.weather

import com.weatheruous.data.network.NetworkConstants
import com.weatheruous.data.network.RetrofitClient

object WeatherAPI {
    val weatherApiService: WeatherApiService by lazy {
        RetrofitClient.getRetrofitWithBaseUrl(NetworkConstants.WEATHER_BASE_URL)
            .create(WeatherApiService::class.java)
    }
}
