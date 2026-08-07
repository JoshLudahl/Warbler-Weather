package com.warbler.feature.weather.ui.main.current

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocalAirport
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.warbler.feature.weather.ui.composables.WeatherStat
import com.warbler.feature.weather.ui.composables.WeatherStatsGrid

@Composable
fun CurrentConditionsScreen(
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Current Weather Conditions") },
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
        WeatherStatsScreen()
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
        }
    }
}

@Composable
fun WeatherStatsScreen() {
    val weatherStats =
        listOf(
            WeatherStat(
                title = "Temperature",
                value = "22°C",
                description = "Feels like 20°C",
                icon = { Icon(Icons.Default.Thermostat, contentDescription = "Temperature") },
            ),
            WeatherStat(
                title = "Humidity",
                value = "65%",
                description = "Moderate moisture",
                icon = { Icon(Icons.Default.WaterDrop, contentDescription = "Humidity") },
            ),
            WeatherStat(
                title = "Wind Speed",
                value = "12 km/h",
                description = "From NW",
                icon = { Icon(Icons.Default.LocalAirport, contentDescription = "Wind") },
            ),
            WeatherStat(
                title = "UV Index",
                value = "4",
                description = "Moderate exposure",
                icon = { Icon(Icons.Default.WbSunny, contentDescription = "UV Index") },
            ),
            WeatherStat(
                title = "Pressure",
                value = "1013 hPa",
                description = "Stable conditions",
                icon = { Icon(Icons.Default.Air, contentDescription = "Pressure") },
            ),
            WeatherStat(
                title = "Visibility",
                value = "10 km",
                description = "Clear view",
                icon = { Icon(Icons.Default.Visibility, contentDescription = "Visibility") },
            ),
        )

    WeatherStatsGrid(
        stats = weatherStats,
        modifier = Modifier.fillMaxSize(),
    )
}
