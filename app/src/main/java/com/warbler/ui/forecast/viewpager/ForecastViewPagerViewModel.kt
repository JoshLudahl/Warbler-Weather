package com.warbler.ui.forecast.viewpager

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.warbler.data.model.weather.Forecasts

class ForecastViewPagerViewModel(
    val forecasts: Forecasts,
) : ViewModel(),
    LifecycleObserver
