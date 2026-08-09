package com.warbler.feature.weather.ui.forecast.daily

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.theme.AppTheme
import com.warbler.feature.weather.ui.composables.DailyForecast
import com.warbler.feature.weather.ui.composables.WeatherForecastList
import com.warbler.feature.weather.ui.main.WeatherUiState

@Composable
fun ForecastScreen(
    weatherUiState: WeatherUiState?,
    onNavigateUp: () -> Unit,
    onForecastItemClick: (Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("8 Day Forecast") },
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
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp),
        ) {
            ForecastScreenContent(weatherUiState, onForecastItemClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPreview() {
    AppTheme(themeMode = ThemeMode.SYSTEM, themeStyle = ThemeStyle.DEFAULT) {
        ForecastScreen(
            weatherUiState = null,
            onNavigateUp = {},
            onForecastItemClick = {},
        )
    }
}

// Usage example
@Composable
fun ForecastScreenContent(
    weatherUiState: WeatherUiState?,
    onForecastItemClick: (Int) -> Unit,
) {
    val forecasts =
        weatherUiState?.dailyForecasts?.map {
            DailyForecast(
                day = it.day,
                icon = it.iconRes,
                highTemp = it.highTemp,
                lowTemp = it.lowTemp,
                description = it.description,
            )
        } ?: emptyList()

    WeatherForecastList(
        forecasts = forecasts,
        onItemClick = onForecastItemClick,
        modifier = Modifier.fillMaxSize(),
    )
}
