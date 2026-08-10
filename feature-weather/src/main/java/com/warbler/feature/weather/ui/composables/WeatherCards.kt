package com.warbler.feature.weather.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTypography
import com.warbler.core.theme.error
import com.warbler.core.theme.temp_high
import com.warbler.core.theme.temp_low
import com.warbler.data.model.weather.aqiColorForLevel
import com.warbler.data.model.weather.aqiLevelInfoList
import com.warbler.feature.weather.R
import com.warbler.feature.weather.ui.main.WeatherUiState

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
                    color = MaterialTheme.colorScheme.primary,
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
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
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
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
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
fun WeatherStatItem(
    icon: Any,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (icon) {
            is ImageVector -> {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            is Int -> {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    colorFilter =
                        ColorFilter.tint(
                            MaterialTheme.colorScheme.primary,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun WeatherStats(
    weatherUiState: WeatherUiState,
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
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherStatItem(
                icon = R.drawable.ic_wind,
                value = weatherUiState.wind,
                label = "Wind",
            )

            WeatherStatItem(
                icon = R.drawable.ic_wi_humidity,
                value = weatherUiState.humidity,
                label = "Humidity",
            )

            WeatherStatItem(
                icon = R.drawable.ic_wi_umbrella,
                value = weatherUiState.rain,
                label = "Rain",
            )
        }
    }
}

@Composable
fun HourlyForecastCard(
    time: String,
    temperature: String,
    icon: Int,
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
                color = MaterialTheme.colorScheme.primary,
            )

            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
            )

            Text(
                text = temperature,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyForecastCard(
    day: String,
    high: Int,
    low: Int,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier =
            modifier
                .height(160.dp),
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                color = MaterialTheme.colorScheme.primary,
            )

            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(56.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "$low°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = temp_low,
                )

                Icon(
                    imageVector = Icons.Rounded.SwapVert,
                    contentDescription = "High and Low",
                    modifier = Modifier.size(20.dp).scale(scaleX = -1f, scaleY = 1f),
                )

                Text(
                    text = "$high°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = temp_high,
                )
            }
        }
    }
}

// Data model for weather stats
data class WeatherStat(
    val title: String,
    val value: String,
    val description: String,
    val icon: @Composable () -> Unit,
)

@Composable
fun WeatherStatsGrid(
    stats: List<WeatherStat>,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        modifier =
            modifier
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(stats) { stat ->
            WeatherStatCard(stat = stat)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeatherStatCard(stat: WeatherStat) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Icon
                Box(
                    modifier =
                        Modifier
                            .size(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    stat.icon()
                }

                Column {
                    // Title
                    Text(
                        text = stat.title,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                    )

                    // Value
                    Text(
                        text = stat.value,
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // Description
            Text(
                text = stat.description,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// Data model for daily forecast
data class DailyForecast(
    val day: String,
    val icon: Int,
    val highTemp: Int,
    val lowTemp: Int,
    val description: String,
)

@Composable
fun WeatherForecastList(
    forecasts: List<DailyForecast>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            // Group items into rows of 2 for landscape
            val rows = forecasts.chunked(2)
            rows.mapIndexed { index, rowItems ->
                Row(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) {
                    rowItems.forEachIndexed { rowIndex, forecast ->
                        val itemIndex = index * 2 + rowIndex
                        ForecastRow(
                            forecast = forecast,
                            onClick = { onItemClick(itemIndex) },
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .fillMaxHeight(),
                        )
                    }
                    // Fill empty space if the last row isn't full
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            forecasts.forEachIndexed { index, forecast ->
                ForecastRow(
                    forecast = forecast,
                    onClick = { onItemClick(index) },
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastRow(
    forecast: DailyForecast,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Day and Description (Left)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = forecast.day,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = forecast.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Weather icon (Center)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = forecast.icon),
                    contentDescription = forecast.description,
                    modifier = Modifier.size(48.dp),
                )
            }

            // Temperature range with separator icon (Right)
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            ) {
                // Low temp
                Text(
                    text = "${forecast.lowTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = temp_low.copy(alpha = 0.7f),
                )

                // Separator icon
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )

                // High temp
                Text(
                    text = "${forecast.highTemp}°",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = temp_high,
                )
            }
        }
    }
}
