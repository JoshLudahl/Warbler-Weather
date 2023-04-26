package com.warbler.data.model.weather

import androidx.annotation.IdRes

data class WeatherForecast(
    val dayOfWeek: String,
    val hi: String,
    val low: String,
    @IdRes val icon: Int
)
