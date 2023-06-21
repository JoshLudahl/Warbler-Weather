package com.warbler.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warbler.data.model.weather.Daily

class ForecastViewModelFactory(
    val daily: Daily
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return ForecastViewModel(daily = daily) as T
    }
}
