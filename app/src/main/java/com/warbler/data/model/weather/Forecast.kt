package com.warbler.data.model.weather

import android.os.Parcelable
import androidx.annotation.Keep
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Forecast(
    val daily: Daily,
    val speed: Speed,
    val temperature: Temperature,
    val timeZoneOffset: Int,
) : Parcelable

@Keep
@Parcelize
data class Forecasts(
    val forecasts: List<Forecast>,
) : Parcelable
