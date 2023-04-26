package com.warbler.ui.settings

import androidx.annotation.IdRes
import com.warbler.R

enum class Temperature(
    @IdRes val id: Int
) {
    CELSIUS(R.id.radio_celsius),
    FAHRENHEIT(R.id.radio_fahrenheit),
    KELVIN(R.id.radio_kelvin)
}
