package com.warbler.data.network

import android.content.Context
import com.warbler.R

object NetworkConstants {
    private var weatherBaseUrl: String = ""
    private var weatherApiKey: String = ""

    fun init(context: Context) {
        weatherBaseUrl = context.getString(R.string.WEATHER_BASE_URL)
        weatherApiKey = context.getString(R.string.WEATHER_API_KEY)
    }

    val WEATHER_BASE_URL get() = weatherBaseUrl
    val WEATHER_API_KEY get() = weatherApiKey
    const val CITY_SEARCH_LIMIT = 10
}
