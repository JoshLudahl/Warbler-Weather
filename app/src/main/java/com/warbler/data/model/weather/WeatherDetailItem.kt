package com.warbler.data.model.weather

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class WeatherDetailItem(
    @DrawableRes val icon: Int,
    val value: String,
    @StringRes val label: Int,
)
