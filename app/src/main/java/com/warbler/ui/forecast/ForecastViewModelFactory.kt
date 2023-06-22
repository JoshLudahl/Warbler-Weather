package com.warbler.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warbler.data.model.weather.Daily
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature

class ForecastViewModelFactory(
    val daily: Daily,
    val speedUnits: Speed,
    val tempUnits: Temperature
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return ForecastViewModel(
            daily = daily,
            speedUnits = speedUnits,
            tempUnits = tempUnits
        ) as T
    }
}
