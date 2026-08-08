package com.warbler.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.ClockUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        private val temperatureKey = intPreferencesKey("temperature_unit")
        private val speedKey = intPreferencesKey("speed_unit")
        private val accumulationKey = intPreferencesKey("accumulation_unit")
        private val clockKey = intPreferencesKey("clock_unit")
        private val themeModeKey = intPreferencesKey("theme_mode")
        private val themeStyleKey = intPreferencesKey("theme_style")

        val temperatureUnit: Flow<TemperatureUnit> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[temperatureKey] ?: 0) {
                        0 -> TemperatureUnit.CELSIUS
                        1 -> TemperatureUnit.FAHRENHEIT
                        2 -> TemperatureUnit.KELVIN
                        else -> TemperatureUnit.FAHRENHEIT
                    }
                }

        val speedUnit: Flow<SpeedUnit> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[speedKey] ?: 0) {
                        0 -> SpeedUnit.MPH
                        1 -> SpeedUnit.MPS
                        2 -> SpeedUnit.KPH
                        else -> SpeedUnit.MPH
                    }
                }

        val accumulationUnit: Flow<AccumulationUnit> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[accumulationKey] ?: 0) {
                        0 -> AccumulationUnit.INCHES_PER_HOUR
                        1 -> AccumulationUnit.MILLIMETERS_PER_HOUR
                        else -> AccumulationUnit.MILLIMETERS_PER_HOUR
                    }
                }

        val clockUnit: Flow<ClockUnit> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[clockKey] ?: 0) {
                        0 -> ClockUnit.H12
                        1 -> ClockUnit.H24
                        else -> ClockUnit.H12
                    }
                }

        val themeMode: Flow<ThemeMode> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[themeModeKey] ?: 0) {
                        0 -> ThemeMode.LIGHT
                        1 -> ThemeMode.DARK
                        2 -> ThemeMode.SYSTEM
                        else -> ThemeMode.SYSTEM
                    }
                }

        val themeStyle: Flow<ThemeStyle> =
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .map { prefs ->
                    when (prefs[themeStyleKey] ?: 0) {
                        0 -> ThemeStyle.DEFAULT
                        1 -> ThemeStyle.DYNAMIC
                        else -> ThemeStyle.DEFAULT
                    }
                }

        suspend fun saveTemperatureUnit(unit: TemperatureUnit) {
            val value =
                when (unit) {
                    TemperatureUnit.CELSIUS -> 0
                    TemperatureUnit.FAHRENHEIT -> 1
                    TemperatureUnit.KELVIN -> 2
                }
            dataStore.edit { it[temperatureKey] = value }
        }

        suspend fun saveSpeedUnit(unit: SpeedUnit) {
            val value =
                when (unit) {
                    SpeedUnit.MPH -> 0
                    SpeedUnit.MPS -> 1
                    SpeedUnit.KPH -> 2
                }
            dataStore.edit { it[speedKey] = value }
        }

        suspend fun saveAccumulationUnit(unit: AccumulationUnit) {
            val value =
                when (unit) {
                    AccumulationUnit.INCHES_PER_HOUR -> 0
                    AccumulationUnit.MILLIMETERS_PER_HOUR -> 1
                }
            dataStore.edit { it[accumulationKey] = value }
        }

        suspend fun saveClockUnit(unit: ClockUnit) {
            val value =
                when (unit) {
                    ClockUnit.H12 -> 0
                    ClockUnit.H24 -> 1
                }
            dataStore.edit { it[clockKey] = value }
        }

        suspend fun saveThemeMode(mode: ThemeMode) {
            val value =
                when (mode) {
                    ThemeMode.LIGHT -> 0
                    ThemeMode.DARK -> 1
                    ThemeMode.SYSTEM -> 2
                }
            dataStore.edit { it[themeModeKey] = value }
        }

        suspend fun saveThemeStyle(style: ThemeStyle) {
            val value =
                when (style) {
                    ThemeStyle.DEFAULT -> 0
                    ThemeStyle.DYNAMIC -> 1
                }
            dataStore.edit { it[themeStyleKey] = value }
        }
    }
