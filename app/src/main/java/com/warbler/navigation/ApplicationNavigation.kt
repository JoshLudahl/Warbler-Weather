package com.warbler.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.warbler.core.theme.AppTheme
import com.warbler.feature.location.ui.LocationScreen
import com.warbler.feature.settings.ui.SettingsScreen
import com.warbler.feature.weather.ui.forecast.ForecastScreen
import com.warbler.feature.weather.ui.forecast.ForecastViewPagerScreen
import com.warbler.feature.weather.ui.main.MainWeatherScreen
import com.warbler.ui.main.MainWeatherViewModel

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
                entry<Destinations.Home> {
                    val viewModel: MainWeatherViewModel = hiltViewModel()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    MainWeatherScreen(
                        weatherUiState = weatherUiState,
                        onLocationClick = { backStack.add(Destinations.Location) },
                        onSettingsClick = { backStack.add(Destinations.Settings) },
                    )
                }
                entry<Destinations.Forecast> { ForecastScreen() }
                entry<Destinations.ForecastViewPager> { ForecastViewPagerScreen() }
                entry<Destinations.Location> { LocationScreen(onNavigateBack = { backStack.removeLastOrNull() }) }
                entry<Destinations.Settings> { SettingsScreen(onNavigateUp = { backStack.removeLastOrNull() }) }
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
