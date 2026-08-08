package com.warbler.feature.weather.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.warbler.feature.weather.ui.main.WeatherUiState

@Composable
fun SectionTitle(
    title: String,
    hasMore: Boolean = true,
    onClickMore: () -> Unit = {},
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
        )

        if (hasMore) {
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = onClickMore,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "More",
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
fun HourlyForecastSection(
    weatherUiState: WeatherUiState,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(weatherUiState.hourlyForecasts) { forecast ->
            HourlyForecastCard(
                time = forecast.time,
                temperature = forecast.temperature,
                icon = forecast.iconRes,
            )
        }
    }
}

@Composable
fun ForecastSection(
    weatherUiState: WeatherUiState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        weatherUiState.dailyForecasts.take(3).forEach { day ->
            DailyForecastCard(
                day = day.day,
                high = day.highTemp,
                low = day.lowTemp,
                icon = day.iconRes,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
