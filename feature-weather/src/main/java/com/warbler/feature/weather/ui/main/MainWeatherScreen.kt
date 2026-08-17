package com.warbler.feature.weather.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.warbler.feature.weather.ui.composables.CustomInformationBanner
import com.warbler.feature.weather.ui.composables.ForecastSection
import com.warbler.feature.weather.ui.composables.HourlyForecastSection
import com.warbler.feature.weather.ui.composables.MainWeatherCard
import com.warbler.feature.weather.ui.composables.NavigationMenuOptions
import com.warbler.feature.weather.ui.composables.SectionTitle
import com.warbler.feature.weather.ui.composables.SunAndMoonCard
import com.warbler.feature.weather.ui.composables.WeatherAlert
import com.warbler.feature.weather.ui.composables.WeatherStats

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainWeatherScreen(
    weatherUiState: WeatherUiState?,
    isOffline: Boolean = false,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onForecastClick: () -> Boolean,
    onHourlyClick: () -> Boolean,
    onStatsNextClick: () -> Boolean,
    onForecastItemClick: (Int) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { onLocationClick() }) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location Icon",
                            )
                        }

                        Text(
                            weatherUiState?.locationName ?: "Warbler Weather",
                            fontFamily = AppTypography.titleMedium.fontFamily,
                            fontWeight = FontWeight.Bold,
                            modifier =
                                Modifier.clickable {
                                    onLocationClick()
                                },
                        )
                    }
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
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isOffline) {
                val offlineMessage =
                    weatherUiState?.lastRefreshedAt?.let {
                        "Offline. Last updated at $it"
                    } ?: "Application is offline"

                CustomInformationBanner(
                    message = offlineMessage,
                    onClick = { },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.CloudOff,
                            contentDescription = "Offline Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                )
            }

            if (weatherUiState != null) {
                if (weatherUiState.hasAlerts) {
                    WeatherAlert(weatherUiState = weatherUiState)
                }

                SectionTitle(title = "Current Conditions") { onStatsNextClick() }
                MainWeatherCard(
                    weatherUiState = weatherUiState,
                    icon = weatherUiState.iconRes,
                    modifier = Modifier,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (weatherUiState.hasAqi) {
                    AqiInformation(weatherUiState = weatherUiState)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                WeatherStats(weatherUiState = weatherUiState)

                SunAndMoonCard(weatherUiState = weatherUiState)

                SectionTitle(title = "Hourly", onClickMore = { onHourlyClick() })
                HourlyForecastSection(weatherUiState = weatherUiState)

                SectionTitle(title = "Forecast", onClickMore = { onForecastClick() })
                ForecastSection(
                    weatherUiState = weatherUiState,
                    onForecastItemClick = onForecastItemClick,
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else if (!isOffline) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularWavyProgressIndicator()
                }
            }
        }
    }
}
