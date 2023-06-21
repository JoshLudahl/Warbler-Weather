package com.warbler.ui.forecast

import androidx.lifecycle.ViewModel
import com.warbler.data.model.weather.Daily

class ForecastViewModel(
    val daily: Daily
) : ViewModel()
