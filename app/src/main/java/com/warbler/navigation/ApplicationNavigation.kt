package com.warbler.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.theme.AppTheme
import com.warbler.core.utilities.launchReviewFlow
import com.warbler.feature.location.ui.LocationScreen
import com.warbler.feature.settings.ui.SettingsScreen
import com.warbler.feature.weather.ui.forecast.daily.ForecastScreen
import com.warbler.feature.weather.ui.forecast.daily.ForecastViewPagerScreen
import com.warbler.feature.weather.ui.forecast.hourly.HourlyScreen
import com.warbler.feature.weather.ui.main.MainWeatherScreen
import com.warbler.feature.weather.ui.main.current.CurrentConditionsScreen
import com.warbler.ui.main.MainWeatherViewModel

@Composable
fun ApplicationNavigation(
    modifier: Modifier = Modifier,
    backStack: NavBackStack<NavKey> = rememberNavBackStack(Destinations.Home),
) {
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(700),
            ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(700),
                )
        },
        popTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(700),
            ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(700),
                )
        },
        predictivePopTransitionSpec = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(700),
            ) togetherWith
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(700),
                )
        },
        entryProvider =
            entryProvider {
                entry<Destinations.Home> {
                    val viewModel: MainWeatherViewModel = hiltViewModel<MainWeatherViewModel>()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    val isOffline by viewModel.isOffline.collectAsState()
                    MainWeatherScreen(
                        weatherUiState = weatherUiState,
                        isOffline = isOffline,
                        onLocationClick = { backStack.add(Destinations.Location) },
                        onSettingsClick = { backStack.add(Destinations.Settings) },
                        onForecastClick = { backStack.add(Destinations.Forecast) },
                        onHourlyClick = { backStack.add(Destinations.Hourly) },
                        onStatsNextClick = { backStack.add(Destinations.Stats) },
                        onForecastItemClick = { index ->
                            backStack.add(Destinations.ForecastViewPager(index))
                        },
                    )
                }
                entry<Destinations.Forecast> {
                    val viewModel: MainWeatherViewModel = hiltViewModel<MainWeatherViewModel>()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    ForecastScreen(
                        weatherUiState = weatherUiState,
                        onNavigateUp = { backStack.removeLastOrNull() },
                        onForecastItemClick = { index ->
                            backStack.add(Destinations.ForecastViewPager(index))
                        },
                    )
                }
                entry<Destinations.ForecastViewPager> {
                    val viewModel: MainWeatherViewModel = hiltViewModel<MainWeatherViewModel>()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    ForecastViewPagerScreen(
                        weatherUiState = weatherUiState,
                        initialPage = it.initialPage,
                        onNavigateUp = { backStack.removeLastOrNull() },
                    )
                }
                entry<Destinations.Hourly> {
                    val viewModel: MainWeatherViewModel = hiltViewModel<MainWeatherViewModel>()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    HourlyScreen(
                        weatherUiState = weatherUiState,
                        onNavigateUp = { backStack.removeLastOrNull() },
                    )
                }
                entry<Destinations.Location> { LocationScreen(onNavigateBack = { backStack.removeLastOrNull() }) }
                entry<Destinations.Settings> {
                    val context = LocalContext.current
                    SettingsScreen(
                        onNavigateUp = { backStack.removeLastOrNull() },
                        onReviewAppClick = { context.launchReviewFlow() },
                    )
                }
                entry<Destinations.Stats> {
                    val viewModel: MainWeatherViewModel = hiltViewModel<MainWeatherViewModel>()
                    val weatherUiState by viewModel.weatherUiState.collectAsState()
                    CurrentConditionsScreen(
                        weatherUiState = weatherUiState,
                        onNavigateUp = { backStack.removeLastOrNull() },
                    )
                }
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun ApplicationNavigationPreview() {
    AppTheme(themeMode = ThemeMode.SYSTEM, themeStyle = ThemeStyle.DEFAULT) {
        ApplicationNavigation()
    }
}
