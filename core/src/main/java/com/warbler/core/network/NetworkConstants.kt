package com.warbler.core.network

object NetworkConstants {
    private var weatherBaseUrl: String = ""
    private var weatherApiKey: String = ""

    fun init(
        baseUrl: String,
        apiKey: String,
    ) {
        weatherBaseUrl = baseUrl
        weatherApiKey = apiKey
    }

    val WEATHER_BASE_URL get() = weatherBaseUrl
    val WEATHER_API_KEY get() = weatherApiKey
    const val CITY_SEARCH_LIMIT = 10
}
