package com.warbler.data.model.weather

import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class HourlyForecastItem(
    val hour: String,
    val description: String,
    val formattedTemp: String,
    @IdRes val icon: Int,
) : Parcelable
