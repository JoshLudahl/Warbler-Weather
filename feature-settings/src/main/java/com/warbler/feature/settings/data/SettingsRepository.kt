package com.warbler.feature.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.warbler.feature.settings.model.AccumulationUnit
import com.warbler.feature.settings.model.SpeedUnit
import com.warbler.feature.settings.model.TemperatureUnit
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
    }
