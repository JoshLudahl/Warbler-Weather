package com.warbler.feature.weather.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTypography
import com.warbler.core.theme.error
import com.warbler.core.theme.temp_high
import com.warbler.core.theme.temp_low
import com.warbler.data.model.weather.aqiColorForLevel
import com.warbler.data.model.weather.aqiLevelInfoList
import com.warbler.feature.weather.R
import com.warbler.feature.weather.ui.main.WeatherUiState
import kotlin.text.ifEmpty

@Composable
fun MainWeatherCard(
    weatherUiState: WeatherUiState,
    icon: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = weatherUiState.description,
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = weatherUiState.temperature,
                    style = AppTypography.displayMedium.copy(fontWeight = FontWeight.Bold),
                )

                // Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = weatherUiState.feelsLike,
                    style = AppTypography.bodySmall,
                )
            }

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
        }
    }
}

@Composable
fun WeatherAlert(
    weatherUiState: WeatherUiState,
) {
    var showAlertDialog by remember { mutableStateOf(false) }
    CustomInformationBanner(
        message = "Weather Alert",
        onClick = { showAlertDialog = true },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Warning,
                contentDescription = "Weather Alert",
                tint = error,
            )
        },
    )

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            confirmButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = weatherUiState.alertTitle.ifEmpty { "Weather Alert" }) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = weatherUiState.alertDescription,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        )
    }
}

@Composable
fun AqiInformation(
    weatherUiState: WeatherUiState,
) {
    var showAqiDialog by remember { mutableStateOf(false) }
    val aqiColor = aqiColorForLevel(weatherUiState.aqiLevel)

    CustomInformationBanner(
        iconColor = aqiColor,
        message = "Air Quality Index is ${weatherUiState.aqiLevel}",
        onClick = { showAqiDialog = true },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_relax),
                contentDescription = "Air Quality Index",
                tint = aqiColor,
            )
        },
    )

    if (showAqiDialog) {
        AlertDialog(
            onDismissRequest = { showAqiDialog = false },
            confirmButton = {
                IconButton(onClick = { showAqiDialog = false }) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = "Air Quality Index - ${weatherUiState.aqiLevel}") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                ) {
                    aqiLevelInfoList.forEach { info ->
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(16.dp)
                                        .background(color = info.color, shape = CircleShape),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = info.description)
                        }
                    }
                }
            },
        )
    }
}

@Composable
fun WeatherStat(
    imageVector: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun WeatherStats(
    weatherUiState: WeatherUiState,
    onStatsNextClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 0.dp, top = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherStat(
                imageVector = Icons.Default.Air,
                value = "10 m/s",
                label = "Wind",
            )

            WeatherStat(
                imageVector = Icons.Default.WaterDrop,
                value = "98%",
                label = "Humidity",
            )

            WeatherStat(
                imageVector = Icons.Default.Umbrella,
                value = "100%",
                label = "Rain",
            )

            IconButton(
                onClick = { onStatsNextClick() },
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = "More",
                )
            }
        }
    }
}

@Composable
fun HourlyForecastCard(
    time: String,
    temperature: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .width(90.dp)
                .height(140.dp),
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )

            Text(
                text = temperature,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
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
fun DailyForecastCard(
    day: String,
    high: Int,
    low: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .height(160.dp),
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$high°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = temp_high,
                )

                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "High and Low",
                    modifier = Modifier.size(20.dp),
                )

                Text(
                    text = "$low°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = temp_low,
                )
            }
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
