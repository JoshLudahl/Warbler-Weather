package com.warbler.ui.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warbler.data.model.weather.Forecast

class ForecastViewModelFactory(
    val forecast: Forecast
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        return ForecastViewModel(
            forecast
        ) as T
    }
}
