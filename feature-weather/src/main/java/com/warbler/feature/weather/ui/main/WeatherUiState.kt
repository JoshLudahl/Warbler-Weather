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
    val wind: String = "",
    val humidity: String = "",
    val rain: String = "",
    val hourlyForecasts: List<HourlyForecastItem> = emptyList(),
    val dailyForecasts: List<DailyForecastItem> = emptyList(),
)

data class HourlyForecastItem(
    val time: String,
    val temperature: String,
    val iconRes: Int,
)

data class DailyForecastItem(
    val day: String,
    val highTemp: Int,
    val lowTemp: Int,
    val iconRes: Int,
    val description: String = "",
)
