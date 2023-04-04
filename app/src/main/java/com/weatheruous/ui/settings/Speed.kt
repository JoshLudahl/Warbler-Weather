package com.weatheruous.ui.settings

import androidx.annotation.IdRes
import com.weatheruous.R

enum class Speed(
    @IdRes val id: Int
) {
    MPH(R.id.radio_mph),
    KPH(R.id.radio_kmh)
}
