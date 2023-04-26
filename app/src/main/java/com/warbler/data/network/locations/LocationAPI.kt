package com.warbler.data.network.locations

import com.warbler.data.network.NetworkConstants
import com.warbler.data.network.RetrofitClient

object LocationAPI {
    val locationApiService: LocationApiService by lazy {
        RetrofitClient.getRetrofitWithBaseUrl(NetworkConstants.WEATHER_BASE_URL)
            .create(LocationApiService::class.java)
    }
}
