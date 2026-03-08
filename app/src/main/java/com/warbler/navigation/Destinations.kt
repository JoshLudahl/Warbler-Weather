package com.warbler.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destinations : NavKey {
    @Serializable
    data object Forecast : Destinations

    @Serializable
    data object Home : Destinations

    @Serializable
    data object Location : Destinations

    @Serializable
    data object Settings : Destinations
}
