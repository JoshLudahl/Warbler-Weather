package com.weatheruous.ui.settings

import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weatheruous.R
import com.weatheruous.utilities.Constants
import com.weatheruous.utilities.DataPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _temperatureUnit = MutableStateFlow(R.id.radio_celsius)
    val temperatureUnit: StateFlow<Int>
        get() = _temperatureUnit

    private val _speedUnit = MutableStateFlow(R.id.radio_mph)
    val speedUnit: StateFlow<Int>
        get() = _speedUnit

    init {
        viewModelScope.launch {
            val unit = DataPref.readIntDataStore(Constants.TEMPERATURE_UNIT, dataStore) ?: 0
            updateTemperatureUnit(unit)

            val speed = DataPref.readIntDataStore(Constants.SPEED_UNIT, dataStore) ?: 0
            updateSpeedUnit(speed)
        }
    }

    private fun updateTemperatureUnit(temperatureUnit: Int) {
        _temperatureUnit.value = when (temperatureUnit) {
            0 -> Temperature.CELSIUS
            1 -> Temperature.FAHRENHEIT
            2 -> Temperature.KELVIN
            else -> Temperature.FAHRENHEIT
        }.id
    }

    private fun updateSpeedUnit(speedUnit: Int) {
        _speedUnit.value = when (speedUnit) {
            0 -> Speed.MPH
            1 -> Speed.KPH
            else -> Speed.MPH
        }.id
    }

    fun handleTemperatureRadioClick(view: View) {
        when (view.id) {
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
            dataStore.edit { preferences ->
                preferences[Constants.TEMPERATURE_UNIT_PREFERENCES] = when (unit) {
                    Temperature.CELSIUS -> 0
                    Temperature.FAHRENHEIT -> 1
                    Temperature.KELVIN -> 2
                }
            }
        }
    }

    fun handleSpeedRadioClick(view: View) {
        when (view.id) {
            R.id.radio_mph -> Speed.MPH
            R.id.radio_kmh -> Speed.KPH
            else -> Speed.MPH // Default
        }.let { speed ->
            saveSpeedUnit(speed)
        }
    }

    private fun saveSpeedUnit(unit: Speed) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[Constants.SPEED_UNIT_PREFERENCES] = when (unit) {
                    Speed.MPH -> 0
                    Speed.KPH -> 1
                }
            }
        }
    }
}
