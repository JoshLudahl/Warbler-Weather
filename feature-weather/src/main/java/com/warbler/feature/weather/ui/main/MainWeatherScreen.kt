package com.warbler.feature.weather.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTypography
import com.warbler.feature.weather.ui.composables.AqiInformation
import com.warbler.feature.weather.ui.composables.ForecastSection
import com.warbler.feature.weather.ui.composables.HourlyForecastSection
import com.warbler.feature.weather.ui.composables.MainWeatherCard
import com.warbler.feature.weather.ui.composables.NavigationMenuOptions
import com.warbler.feature.weather.ui.composables.SectionTitle
import com.warbler.feature.weather.ui.composables.WeatherAlert
import com.warbler.feature.weather.ui.composables.WeatherStats

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainWeatherScreen(
    weatherUiState: WeatherUiState?,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onForecastClick: () -> Boolean,
    onHourlyClick: () -> Boolean,
    onStatsNextClick: () -> Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        weatherUiState?.locationName ?: "Warbler Weather",
                        fontFamily = AppTypography.titleMedium.fontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                },
                subtitle = { Text(weatherUiState?.dateTitle ?: "") },
                actions = {
                    NavigationMenuOptions(
                        onLocationClick = onLocationClick,
                        onSettingsClick = onSettingsClick,
                    )
                },
            )
        },
    ) { innerPadding ->
        if (weatherUiState != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (weatherUiState.hasAlerts) {
                    WeatherAlert(weatherUiState = weatherUiState)
                }

                SectionTitle(title = "Current Conditions", onClickMore = { onStatsNextClick() })
                MainWeatherCard(
                    weatherUiState = weatherUiState,
                    icon = weatherUiState.iconRes,
                    modifier =
                    Modifier,
                )

                if (weatherUiState.hasAqi) {
                    AqiInformation(weatherUiState = weatherUiState)
                }

                WeatherStats(weatherUiState = weatherUiState)

                SectionTitle(title = "Hourly", onClickMore = { onHourlyClick() })
                HourlyForecastSection(weatherUiState = weatherUiState)

                SectionTitle(title = "Forecast", onClickMore = { onForecastClick() })
                ForecastSection(weatherUiState = weatherUiState)
                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularWavyProgressIndicator()
            }
        }
    }
}
