package com.warbler.ui.forecast

import android.util.Log
import androidx.lifecycle.ViewModel
import com.warbler.data.model.weather.Conversion
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Daily
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import kotlin.math.roundToInt

class ForecastViewModel(
    val daily: Daily,
    val speedUnits: Speed,
    val tempUnits: Temperature
) : ViewModel() {
    val maxTemperature = Conversion.fromKelvinToProvidedUnit(
        daily.temp.max,
        tempUnits
    ).roundToInt().toDegrees

    val minTemperature = Conversion.fromKelvinToProvidedUnit(
        daily.temp.min,
        tempUnits
    ).roundToInt().toDegrees

    val icon = daily.weather[0].icon.getIconForCondition
    val description = daily.weather[0].description.capitalizeEachFirst
    init {
        Log.i("ForecastViewModel", "Daily: ${daily.summary}")
    }
}
