package com.warbler.data.model.weather

import android.os.Parcelable
import androidx.annotation.Keep
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Forecast(
    val daily: Daily,
    val speed: SpeedUnit,
    val temperature: TemperatureUnit,
    val timeZoneOffset: Int,
) : Parcelable

@Keep
@Parcelize
data class Forecasts(
    val forecasts: List<Forecast>,
) : Parcelable
