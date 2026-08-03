package com.warbler.feature.weather.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.warbler.core.theme.aqi1Good
import com.warbler.core.theme.aqi2Fair
import com.warbler.core.theme.aqi3Moderate
import com.warbler.core.theme.aqi4Poor
import com.warbler.core.theme.aqi5VeryPoor
import com.warbler.feature.weather.R

private fun aqiColorForLevel(level: Int): Color =
    when (level) {
        1 -> aqi1Good
        2 -> aqi2Fair
        3 -> aqi3Moderate
        4 -> aqi4Poor
        5 -> aqi5VeryPoor
        else -> Color.Gray
    }

private data class AqiLevelInfo(
    val level: Int,
    val color: Color,
    val description: String,
)

private val aqiLevelInfoList =
    listOf(
        AqiLevelInfo(1, aqi1Good, "1 — Good: Air quality is considered satisfactory; little or no risk."),
        AqiLevelInfo(2, aqi2Fair, "2 — Fair: Acceptable air quality; some pollutants may mildly affect sensitive individuals."),
        AqiLevelInfo(3, aqi3Moderate, "3 — Moderate: Sensitive groups may experience irritation or breathing discomfort."),
        AqiLevelInfo(4, aqi4Poor, "4 — Poor: Unhealthy for sensitive groups; increased likelihood of adverse effects."),
        AqiLevelInfo(5, aqi5VeryPoor, "5 — Very Poor: Unhealthy for everyone; significant risk of adverse effects."),
    )

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainWeatherScreen(
    weatherUiState: WeatherUiState?,
    onLocationClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(weatherUiState?.locationName ?: "Warbler Weather") },
                subtitle = { Text(weatherUiState?.dateTitle ?: "") },
                actions = {
                    IconButton(onClick = onLocationClick) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Location")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { innerPadding ->
        if (weatherUiState != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = weatherUiState.iconRes),
                                    contentDescription = weatherUiState.description,
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f),
                                    tint = Color.Unspecified,
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(text = weatherUiState.temperature, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                                Text(text = weatherUiState.feelsLike, fontSize = 18.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = weatherUiState.description, fontSize = 20.sp)

                        if (weatherUiState.hasAlerts) {
                            var showAlertDialog by remember { mutableStateOf(false) }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.clickable { showAlertDialog = true },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Weather Alert",
                                    tint = MaterialTheme.colorScheme.error,
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Weather Alerts Active", color = MaterialTheme.colorScheme.error)
                            }

                            if (showAlertDialog) {
                                AlertDialog(
                                    onDismissRequest = { showAlertDialog = false },
                                    confirmButton = {
                                        IconButton(onClick = { showAlertDialog = false }) {
                                            Text(text = "OK")
                                        }
                                    },
                                    title = { Text(text = weatherUiState.alertTitle.ifEmpty { "Weather Alert" }) },
                                    text = { Text(text = weatherUiState.alertDescription) },
                                )
                            }
                        }
                    }
                }

                if (weatherUiState.hasAqi) {
                    var showAqiDialog by remember { mutableStateOf(false) }
                    val aqiColor = aqiColorForLevel(weatherUiState.aqiLevel)

                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { showAqiDialog = true },
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_relax),
                                contentDescription = "Air Quality Index",
                                tint = aqiColor,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = weatherUiState.aqiValue, color = aqiColor, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Air Quality Index")
                        }
                    }

                    if (showAqiDialog) {
                        AlertDialog(
                            onDismissRequest = { showAqiDialog = false },
                            confirmButton = {
                                IconButton(onClick = { showAqiDialog = false }) {
                                    Text(text = "OK")
                                }
                            },
                            title = { Text(text = "Air Quality Index") },
                            text = {
                                Column {
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
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularWavyProgressIndicator()
            }
        }
    }
}
