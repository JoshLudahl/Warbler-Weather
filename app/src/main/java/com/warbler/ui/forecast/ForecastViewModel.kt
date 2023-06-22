package com.warbler.ui.forecast

import android.util.Log
import androidx.lifecycle.ViewModel
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.getDatOfWeekFromUnixUTC
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Forecast
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import kotlin.math.roundToInt

class ForecastViewModel(
    val forecast: Forecast
) : ViewModel() {
    val maxTemperature = Conversion.fromKelvinToProvidedUnit(
        forecast.daily.temp.max,
        forecast.temperature
    ).roundToInt().toDegrees

    val minTemperature = Conversion.fromKelvinToProvidedUnit(
        forecast.daily.temp.min,
        forecast.temperature
    ).roundToInt().toDegrees

    val icon = forecast.daily.weather[0].icon.getIconForCondition
    val description = forecast.daily.weather[0].description.capitalizeEachFirst
    val dateTitle = getDatOfWeekFromUnixUTC(forecast.daily.dt.toLong())
    init {
        Log.i("ForecastViewModel", "Daily: ${forecast.daily.summary}")
    }
}
