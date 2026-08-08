package com.warbler.feature.settings.ui

import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.ClockUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit

data class SettingsUiState(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.FAHRENHEIT,
    val speedUnit: SpeedUnit = SpeedUnit.MPH,
    val accumulationUnit: AccumulationUnit = AccumulationUnit.MILLIMETERS_PER_HOUR,
    val clockUnit: ClockUnit = ClockUnit.H12,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeStyle: ThemeStyle = ThemeStyle.DEFAULT,
    val appVersion: String = "",
)
