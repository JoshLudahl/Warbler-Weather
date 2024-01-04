package com.warbler.ui.settings

import androidx.annotation.IdRes
import com.warbler.R

enum class Accumulation(
    @IdRes val id: Int,
) {
    INCHES_PER_HOUR(R.id.radio_inph),
    MILLIMETERS_PER_HOUR(R.id.radio_mmph),
}
