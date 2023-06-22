package com.warbler.ui.forecast

import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.warbler.data.model.weather.Conversion.capitalizeEachFirst
import com.warbler.data.model.weather.Daily
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition

@BindingAdapter("weatherIcon")
fun AppCompatImageView.setResourceImage(daily: Daily) {
    setImageResource(daily.weather[0].icon.getIconForCondition)
}

@BindingAdapter("capitalizeEachFirst")
fun TextView.capitalizeEachFirst(string: String) {
    text = string.capitalizeEachFirst
}
