package com.warbler.feature.weather.ui.main

data class WeatherUiState(
    val locationName: String,
    val temperature: String,
    val description: String,
    val dateTitle: String,
    val feelsLike: String,
    val hasAqi: Boolean,
    val aqiValue: String,
    val aqiLevel: Int = 0,
    val hasAlerts: Boolean,
    val alertTitle: String = "",
    val alertDescription: String = "",
    val iconRes: Int,
)
