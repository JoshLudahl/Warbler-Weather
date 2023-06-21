package com.warbler.ui.forecast

import android.util.Log
import androidx.lifecycle.ViewModel
import com.warbler.data.model.weather.Daily

class ForecastViewModel(
    val daily: Daily
) : ViewModel() {
    init {
        Log.i("ForecastViewModel", "Daily: ${daily.summary}")
    }
}
