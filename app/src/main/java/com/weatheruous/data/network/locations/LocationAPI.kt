package com.weatheruous.data.network.locations

import com.weatheruous.data.network.NetworkConstants
import com.weatheruous.data.network.RetrofitClient

object LocationAPI {
    val locationApiService: LocationApiService by lazy {
        RetrofitClient.getRetrofitWithBaseUrl(NetworkConstants.WEATHER_BASE_URL)
            .create(LocationApiService::class.java)
    }
}
