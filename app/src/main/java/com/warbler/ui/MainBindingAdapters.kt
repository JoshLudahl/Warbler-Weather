package com.warbler.ui

import android.content.res.ColorStateList
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.warbler.R

@BindingAdapter("aqiColor")
fun ImageView.setTints(index: Int) {
    when (index) {
        1 -> R.color.AQI_1
        2 -> R.color.AQI_2
        3 -> R.color.AQI_3
        4 -> R.color.AQI_4
        5 -> R.color.AQI_5
        else -> R.color.AQI_1
    }.let {
        setColorFilter(resources.getColor(it, context.theme))
    }
}

@BindingAdapter("aqiColorTextBackground")
fun TextView.setTinty(index: Int) {
    when (index) {
        1 -> R.color.AQI_1
        2 -> R.color.AQI_2
        3 -> R.color.AQI_3
        4 -> R.color.AQI_4
        5 -> R.color.AQI_5
        else -> R.color.AQI_1
    }.let {
        backgroundTintList = ColorStateList.valueOf(resources.getColor(it, context.theme))
    }
}
