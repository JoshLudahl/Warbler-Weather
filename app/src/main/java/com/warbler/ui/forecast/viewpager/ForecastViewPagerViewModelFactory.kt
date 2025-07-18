package com.warbler.ui.forecast.viewpager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.warbler.data.model.weather.Forecasts

class ForecastViewPagerViewModelFactory(
    val forecasts: Forecasts,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ForecastViewPagerViewModel(
            forecasts,
        ) as T
}
