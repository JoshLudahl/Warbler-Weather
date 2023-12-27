package com.warbler.ui.settings

import androidx.annotation.IdRes
import com.warbler.R

enum class Speed(
    @IdRes val id: Int,
) {
    MPH(R.id.radio_mph),
    KPH(R.id.radio_kph),
    MPS(R.id.radio_mps),
}
