package com.warbler.feature.weather.ui.forecast.daily

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbCloudy
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
import androidx.compose.ui.tooling.preview.Preview
import com.warbler.core.theme.AppTheme
import com.warbler.feature.weather.ui.composables.DailyForecast
import com.warbler.feature.weather.ui.composables.WeatherForecastList
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ForecastScreen(
    onNavigateUp: () -> Unit,
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
                    .fillMaxHeight()
                    .padding(paddingValues),
        ) {
            ForecastScreenContent()
            Column {
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForecastScreenPreview() {
    AppTheme {
        ForecastScreen(
            onNavigateUp = {},
        )
    }
}

// Usage example
@Composable
fun ForecastScreenContent() {
    // Helper to generate day names
    fun generateDays(startIndex: Int): List<String> =
        (0 until startIndex).map { offset ->
            LocalDate
                .now()
                .plusDays(offset.toLong())
                .dayOfWeek
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }

    val forecasts =
        listOf(
            DailyForecast(
                day = "Mon",
                icon = Icons.Filled.WbSunny,
                highTemp = 22,
                lowTemp = 15,
                description = "Clear skies",
            ),
            DailyForecast(
                day = "Tue",
                icon = Icons.Filled.CloudQueue,
                highTemp = 24,
                lowTemp = 16,
                description = "Partly cloudy",
            ),
            DailyForecast(
                day = "Wed",
                icon = Icons.Filled.CloudQueue,
                highTemp = 20,
                lowTemp = 14,
                description = "Overcast clouds",
            ),
            DailyForecast(
                day = "Thu",
                icon = Icons.Filled.CloudQueue,
                highTemp = 18,
                lowTemp = 12,
                description = "Light rain",
            ),
            DailyForecast(
                day = "Fri",
                icon = Icons.Filled.Thunderstorm,
                highTemp = 19,
                lowTemp = 13,
                description = "Thunderstorms",
            ),
            DailyForecast(
                day = "Sat",
                icon = Icons.Filled.WbCloudy,
                highTemp = 21,
                lowTemp = 14,
                description = "Cloudy intervals",
            ),
            DailyForecast(
                day = "Sun",
                icon = Icons.Filled.WbSunny,
                highTemp = 23,
                lowTemp = 15,
                description = "Mostly sunny",
            ),
            DailyForecast(
                day = "Mon",
                icon = Icons.Filled.WbCloudy,
                highTemp = 25,
                lowTemp = 17,
                description = "Warm & partly cloudy",
            ),
        )

    WeatherForecastList(
        forecasts = forecasts,
        modifier = Modifier.fillMaxSize(),
    )
}
