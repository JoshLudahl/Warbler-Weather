package com.warbler.data.model.weather

import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class WeatherForecast(
    val dayOfWeek: String,
    val hi: String,
    val index: Int,
    val low: String,
    @IdRes val icon: Int,
) : Parcelable
