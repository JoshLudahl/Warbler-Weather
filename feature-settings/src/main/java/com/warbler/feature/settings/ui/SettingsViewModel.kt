package com.warbler.feature.settings.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.ClockUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit
import com.warbler.feature.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val repository: SettingsRepository,
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(SettingsUiState())
        val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

        init {
            viewModelScope.launch {
                repository.temperatureUnit.collect { unit ->
                    _uiState.update { it.copy(temperatureUnit = unit) }
                }
            }
            viewModelScope.launch {
                repository.speedUnit.collect { unit ->
                    _uiState.update { it.copy(speedUnit = unit) }
                }
            }
            viewModelScope.launch {
                repository.accumulationUnit.collect { unit ->
                    _uiState.update { it.copy(accumulationUnit = unit) }
                }
            }
            viewModelScope.launch {
                repository.clockUnit.collect { unit ->
                    _uiState.update { it.copy(clockUnit = unit) }
                }
            }
            viewModelScope.launch {
                repository.themeMode.collect { mode ->
                    _uiState.update { it.copy(themeMode = mode) }
                }
            }
            viewModelScope.launch {
                repository.themeStyle.collect { style ->
                    _uiState.update { it.copy(themeStyle = style) }
                }
            }
            _uiState.update {
                it.copy(appVersion = "Beta Version ${getVersionName()}")
            }
        }

        private fun getVersionName(): String =
            try {
                val packageInfo =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
                    } else {
                        @Suppress("DEPRECATION")
                        context.packageManager.getPackageInfo(context.packageName, 0)
                    }
                packageInfo.versionName ?: ""
            } catch (e: PackageManager.NameNotFoundException) {
                ""
            }

        fun onTemperatureUnitSelected(unit: TemperatureUnit) {
            viewModelScope.launch {
                repository.saveTemperatureUnit(unit)
            }
        }

        fun onSpeedUnitSelected(unit: SpeedUnit) {
            viewModelScope.launch {
                repository.saveSpeedUnit(unit)
            }
        }

        fun onAccumulationUnitSelected(unit: AccumulationUnit) {
            viewModelScope.launch {
                repository.saveAccumulationUnit(unit)
            }
        }

        fun onClockUnitSelected(unit: ClockUnit) {
            viewModelScope.launch {
                repository.saveClockUnit(unit)
            }
        }

        fun onThemeModeSelected(mode: ThemeMode) {
            viewModelScope.launch {
                repository.saveThemeMode(mode)
            }
        }

        fun onThemeStyleSelected(style: ThemeStyle) {
            viewModelScope.launch {
                repository.saveThemeStyle(style)
            }
        }
    }
