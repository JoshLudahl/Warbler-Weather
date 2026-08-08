package com.warbler.feature.weather.ui.main.current

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
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
import androidx.compose.ui.unit.dp
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
                title = { Text("Current Conditions") },
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
    val weatherStats =
        listOf(
            WeatherStat(
                title = "Wind Speed",
                value = weatherUiState.wind,
                description = "Current wind speed",
                icon = { Icon(Icons.Default.Air, contentDescription = "Wind Speed") },
            ),
            WeatherStat(
                title = "Humidity",
                value = weatherUiState.humidity,
                description = "Current humidity",
                icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Humidity") },
            ),
            WeatherStat(
                title = "Rain",
                value = weatherUiState.rain,
                description = "Precipitation probability",
                icon = { Icon(Icons.Default.Umbrella, contentDescription = "Rain") },
            ),
            WeatherStat(
                title = "UV Index",
                value = weatherUiState.uvIndex,
                description = "Solar radiation level",
                icon = { Icon(Icons.Default.WbSunny, contentDescription = "UV Index") },
            ),
            WeatherStat(
                title = "Pressure",
                value = weatherUiState.pressure,
                description = "Atmospheric pressure",
                icon = { Icon(Icons.Default.Compress, contentDescription = "Pressure") },
            ),
            WeatherStat(
                title = "Visibility",
                value = weatherUiState.visibility,
                description = "Maximum visual range",
                icon = { Icon(Icons.Default.Visibility, contentDescription = "Visibility") },
            ),
            WeatherStat(
                title = "Clouds",
                value = weatherUiState.clouds,
                description = "Cloud cover percentage",
                icon = { Icon(Icons.Default.Cloud, contentDescription = "Clouds") },
            ),
            WeatherStat(
                title = "Dew Point",
                value = weatherUiState.dewPoint,
                description = "Atmospheric moisture",
                icon = { Icon(Icons.Default.DeviceThermostat, contentDescription = "Dew Point") },
            ),
            WeatherStat(
                title = "Sunrise",
                value = weatherUiState.sunrise,
                description = "Time of sunrise",
                icon = { Icon(Icons.Default.WbTwilight, contentDescription = "Sunrise") },
            ),
            WeatherStat(
                title = "Sunset",
                value = weatherUiState.sunset,
                description = "Time of sunset",
                icon = { Icon(Icons.Default.WbTwilight, contentDescription = "Sunset") },
            ),
        )

    WeatherStatsGrid(
        stats = weatherStats,
        modifier = Modifier.height(800.dp), // Height to accommodate grid items
    )
}
