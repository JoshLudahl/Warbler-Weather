package com.warbler.feature.weather.ui.forecast.hourly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.CartesianChartHost
import com.patrykandpatrick.vico.compose.chart.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.chart.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.lineSeries
import com.warbler.feature.weather.ui.composables.HourlyForecastSection
import com.warbler.feature.weather.ui.main.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourlyScreen(
    weatherUiState: WeatherUiState?,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Next 48 Hours") },
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
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (weatherUiState != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HourlyForecastSection(weatherUiState = weatherUiState)

                val hourlyData = weatherUiState.hourlyForecasts.take(12)

                if (hourlyData.any { it.accumulation > 0 }) {
                    WeatherBarChart(
                        title = "Precipitation Accumulation (${weatherUiState.accumulationUnit})",
                        data = hourlyData.map { it.accumulation },
                        labels = hourlyData.map { it.time },
                        color = Color(0xFF2196F3),
                    )
                } else {
                    PrecipitationNoneExpected(
                        title = "Precipitation Accumulation (${weatherUiState.accumulationUnit})",
                        size = hourlyData.size,
                    )
                }

                WeatherChart(
                    title = "Humidity (%)",
                    data = hourlyData.map { it.humidity.toFloat() },
                    labels = hourlyData.map { it.time },
                    color = Color(0xFF00BCD4),
                )

                WeatherChart(
                    title = "Wind Speed (${weatherUiState.windUnit})",
                    data = hourlyData.map { it.windSpeed },
                    labels = hourlyData.map { it.time },
                    color = Color(0xFF9E9E9E),
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PrecipitationNoneExpected(
    title: String,
    size: Int,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth().height(100.dp),
            shape = RoundedCornerShape(30.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center,
            ) {
                Text(
                    text = "None expected for the next $size hours",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun WeatherBarChart(
    title: String,
    data: List<Float>,
    labels: List<String>,
    color: Color,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                series(data)
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(30.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            CartesianChartHost(
                chart =
                    rememberCartesianChart(
                        rememberColumnCartesianLayer(
                            columns =
                                listOf(
                                    rememberLineComponent(
                                        color = color,
                                        thickness = 8.dp,
                                        shape = Shapes.roundedCornerShape(allPercent = 40),
                                    ),
                                ),
                        ),
                        startAxis = rememberStartAxis(),
                        bottomAxis =
                            rememberBottomAxis(
                                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2),
                                valueFormatter = { x, _, _ -> labels.getOrNull(x.toInt()) ?: "" },
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
fun WeatherChart(
    title: String,
    data: List<Float>,
    labels: List<String>,
    color: Color,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data)
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(30.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
        ) {
            CartesianChartHost(
                chart =
                    rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lines =
                                listOf(
                                    LineCartesianLayer.LineSpec(
                                        shader = DynamicShaders.color(color),
                                    ),
                                ),
                        ),
                        startAxis = rememberStartAxis(),
                        bottomAxis =
                            rememberBottomAxis(
                                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2),
                                valueFormatter = { x, _, _ -> labels.getOrNull(x.toInt()) ?: "" },
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
