package com.warbler.data.network.weather

import com.warbler.data.network.NetworkConstants
import com.warbler.data.network.RetrofitClient

object WeatherAPI {
    val weatherApiService: WeatherApiService by lazy {
        RetrofitClient.getRetrofitWithBaseUrl(NetworkConstants.WEATHER_BASE_URL)
            .create(WeatherApiService::class.java)
    }
}
