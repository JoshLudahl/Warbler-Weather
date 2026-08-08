package com.warbler.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.feature.settings.data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        repository: SettingsRepository,
    ) : ViewModel() {
        val themeState: StateFlow<ThemeState> =
            combine(
                repository.themeMode,
                repository.themeStyle,
            ) { mode, style ->
                ThemeState(mode, style)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = ThemeState(),
            )
    }

data class ThemeState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val themeStyle: ThemeStyle = ThemeStyle.DEFAULT,
)
