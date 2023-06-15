package com.warbler.data.model.weather

import androidx.annotation.DrawableRes

data class WeatherDetailItem(
    @DrawableRes val icon: Int,
    val value: String
)
