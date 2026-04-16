package com.warbler.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.warbler.core.theme.AppTheme
import com.warbler.feature.location.ui.LocationScreen
import com.warbler.feature.settings.ui.SettingsScreen
import com.warbler.feature.weather.ui.ForecastScreen
import com.warbler.feature.weather.ui.WeatherScreen

@Composable
fun ApplicationNavigation(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(Destinations.Home),
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry<Destinations.Home> { WeatherScreen() }
                entry<Destinations.Forecast> { ForecastScreen() }
                entry<Destinations.Location> { LocationScreen() }
                entry<Destinations.Settings> { SettingsScreen() }
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun ApplicationNavigationPreview() {
    AppTheme(dynamicColor = false) {
        ApplicationNavigation()
    }
}
