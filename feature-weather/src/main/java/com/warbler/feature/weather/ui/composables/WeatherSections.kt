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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SectionTitle(
    title: String,
    hasMore: Boolean = true,
    onClickMore: () -> Unit = {},
) {
    Row(
        modifier = Modifier
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
fun HourlyForecastSection() {
    // MOCK DATA::::
    data class HourlyForecast(
        val time: String,
        val temp: Int,
        val icon: ImageVector,
    )

    val hourlyForecasts =
        listOf(
            HourlyForecast("10 am", 16, Icons.Default.Cloud),
            HourlyForecast("11 am", 17, Icons.Default.Cloud),
            HourlyForecast("12 pm", 18, Icons.Default.Thunderstorm),
            HourlyForecast("01 pm", 19, Icons.Default.Thunderstorm),
        )

    // END MOCK DATA

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        items(hourlyForecasts) { forecast ->

            HourlyForecastCard(
                time = forecast.time,
                temperature = "${forecast.temp}°",
                icon = forecast.icon,
            )
        }
    }
}

@Composable
fun ForecastSection() {
    // MOCK DATA
    data class DailyForecast(
        val day: String,
        val high: Int,
        val low: Int,
        val icon: ImageVector,
    )

    val forecast =
        listOf(
            DailyForecast("Monday", 22, 14, Icons.Default.WbSunny),
            DailyForecast("Tuesday", 20, 12, Icons.Default.Cloud),
            DailyForecast("Wednesday", 18, 10, Icons.Default.Thunderstorm),
        )

    // END MOCK DATA

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        forecast.forEach { day ->
            DailyForecastCard(
                day = day.day,
                high = day.high,
                low = day.low,
                icon = day.icon,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
