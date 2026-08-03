package com.warbler.feature.weather.ui.main

data class WeatherUiState(
    val locationName: String,
    val temperature: String,
    val description: String,
    val dateTitle: String,
    val feelsLike: String,
    val hasAqi: Boolean,
    val aqiValue: String,
    val hasAlerts: Boolean,
    val iconRes: Int,
)
