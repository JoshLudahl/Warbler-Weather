package com.warbler.feature.settings.ui

import com.warbler.feature.settings.model.AccumulationUnit
import com.warbler.feature.settings.model.SpeedUnit
import com.warbler.feature.settings.model.TemperatureUnit

data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.FAHRENHEIT,
    val speedUnit: SpeedUnit = SpeedUnit.MPH,
    val accumulationUnit: AccumulationUnit = AccumulationUnit.MILLIMETERS_PER_HOUR,
    val appVersion: String = "",
)
