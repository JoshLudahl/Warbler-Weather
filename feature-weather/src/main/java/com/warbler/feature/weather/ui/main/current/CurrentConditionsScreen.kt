package com.warbler.feature.weather.ui.main.current

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTypography
import com.warbler.feature.weather.R
import com.warbler.feature.weather.ui.composables.AqiInformation
import com.warbler.feature.weather.ui.composables.MainWeatherCard
import com.warbler.feature.weather.ui.composables.WeatherStat
import com.warbler.feature.weather.ui.composables.WeatherStatsGrid
import com.warbler.feature.weather.ui.main.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentConditionsScreen(
    weatherUiState: WeatherUiState?,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Current Conditions")
                },
                subtitle = {
                    Text(
                        weatherUiState?.locationName ?: "Warbler Weather",
                        fontFamily = AppTypography.titleMedium.fontFamily,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        if (weatherUiState != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                MainWeatherCard(
                    weatherUiState = weatherUiState,
                    icon = weatherUiState.iconRes,
                )

                if (weatherUiState.hasAqi) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AqiInformation(weatherUiState = weatherUiState)
                }

                Spacer(modifier = Modifier.height(16.dp))

                WeatherStatsScreen(weatherUiState)
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularWavyProgressIndicator()
            }
        }
    }
}

@Composable
fun WeatherStatsScreen(weatherUiState: WeatherUiState) {
    val iconSize = 48.dp
    val weatherStats =
        listOf(
            WeatherStat(
                title = "Wind Speed",
                value = weatherUiState.wind,
                description = "Current wind speed",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wind),
                        contentDescription = "Wind Speed",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Humidity",
                value = weatherUiState.humidity,
                description = "Current humidity",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_humidity),
                        contentDescription = "Humidity",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Rain",
                value = weatherUiState.rain,
                description = "Current precipitation",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_umbrella),
                        contentDescription = "Rain",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "UV Index",
                value = weatherUiState.uvIndex,
                description = "Solar radiation level",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_day_sunny),
                        contentDescription = "UV Index",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Pressure",
                value = weatherUiState.pressure,
                description = "Atmospheric pressure",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_barometer),
                        contentDescription = "Pressure",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Visibility",
                value = weatherUiState.visibility,
                description = "Maximum visual range",
                icon = {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Visibility",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Clouds",
                value = weatherUiState.clouds,
                description = "Cloud cover percentage",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_cloud),
                        contentDescription = "Clouds",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Dew Point",
                value = weatherUiState.dewPoint,
                description = "Atmospheric moisture",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_raindrops),
                        contentDescription = "Dew Point",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Sunrise",
                value = weatherUiState.sunrise,
                description = "Time of sunrise",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_sunrise),
                        contentDescription = "Sunrise",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Sunset",
                value = weatherUiState.sunset,
                description = "Time of sunset",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_sunset),
                        contentDescription = "Sunset",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Moonrise",
                value = weatherUiState.moonrise,
                description = "Time of moonrise",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_moonrise),
                        contentDescription = "Moonrise",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
            WeatherStat(
                title = "Moonset",
                value = weatherUiState.moonset,
                description = "Time of moonset",
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wi_moonset),
                        contentDescription = "Moonset",
                        modifier = Modifier.size(iconSize),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
            ),
        )

    WeatherStatsGrid(
        stats = weatherStats,
    )
}
