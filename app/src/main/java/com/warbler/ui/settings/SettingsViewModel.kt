package com.warbler.ui.settings

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.BuildConfig
import com.warbler.R
import com.warbler.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : ViewModel() {
        private val _temperatureUnit = MutableStateFlow(R.id.radio_celsius)
        val temperatureUnit: StateFlow<Int>
            get() = _temperatureUnit

        private val _speedUnit = MutableStateFlow(R.id.radio_mph)
        val speedUnit: StateFlow<Int>
            get() = _speedUnit

        private val _appVersion = MutableStateFlow("Beta Version ${BuildConfig.VERSION_NAME}")
        val appVersion get() = _appVersion

        init {
            viewModelScope.launch {
                DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore).collect { unit ->
                    updateTemperatureUnit(unit)
                }
            }

            viewModelScope.launch {
                DataPref.readIntDataStoreFlow(DataPref.SPEED_UNIT, dataStore).collect { unit ->
                    updateSpeedUnit(unit)
                }
            }
        }

        private fun updateTemperatureUnit(temperatureUnit: Int) {
            _temperatureUnit.value =
                when (temperatureUnit) {
                    0 -> Temperature.CELSIUS
                    1 -> Temperature.FAHRENHEIT
                    2 -> Temperature.KELVIN
                    else -> Temperature.FAHRENHEIT
                }.id
        }

        private fun updateSpeedUnit(speedUnit: Int) {
            Log.i("Speed Unit", "Speed Unit is: $speedUnit")
            _speedUnit.value =
                when (speedUnit) {
                    0 -> Speed.MPH
                    1 -> Speed.MPS
                    2 -> Speed.KPH
                    else -> Speed.MPH
                }.id
        }

        fun handleTemperatureRadioClick(viewId: Int) {
            when (viewId) {
                R.id.radio_celsius -> Temperature.CELSIUS
                R.id.radio_fahrenheit -> Temperature.FAHRENHEIT
                R.id.radio_kelvin -> Temperature.KELVIN
                else -> Temperature.FAHRENHEIT // Default
            }.let { temperature ->
                saveTemperatureUnit(temperature)
            }
        }

        private fun saveTemperatureUnit(unit: Temperature) {
            viewModelScope.launch {
                when (unit) {
                    Temperature.CELSIUS -> 0
                    Temperature.FAHRENHEIT -> 1
                    Temperature.KELVIN -> 2
                }.let { value ->
                    DataPref.saveIntDataStore(DataPref.TEMPERATURE_UNIT, value, dataStore)
                }
            }
        }

        fun handleSpeedRadioClick(viewId: Int) {
            when (viewId) {
                R.id.radio_mph -> Speed.MPH
                R.id.radio_kph -> Speed.KPH
                R.id.radio_mps -> Speed.MPS
                else -> Speed.MPH // Default
            }.let { speed ->
                Log.i("Speed Unit", "Handle Speed Unit Function: ${speed.name}")
                saveSpeedUnit(speed)
            }
        }

        private fun saveSpeedUnit(unit: Speed) {
            viewModelScope.launch {
                when (unit) {
                    Speed.MPH -> 0
                    Speed.MPS -> 1
                    Speed.KPH -> 2
                }.let { value ->
                    DataPref.saveIntDataStore(DataPref.SPEED_UNIT, value, dataStore)
                }
            }
        }
    }
