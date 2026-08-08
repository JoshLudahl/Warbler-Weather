package com.warbler.feature.weather.ui.forecast.hourly

import android.util.Log
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
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.component.shape.shader.color
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.columnSeries
import com.patrykandpatrick.vico.core.model.lineSeries
import com.warbler.core.theme.temp_low
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
            Log.d("HourlyScreen", "weatherUiState: $weatherUiState")
            if (weatherUiState != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HourlyForecastSection(weatherUiState = weatherUiState)

                val hourlyData = weatherUiState.hourlyForecasts.take(12)

                if (hourlyData.any { it.accumulation > 0 }) {
                    WeatherMultiBarChart(
                        title = "Precipitation Accumulation (${weatherUiState.accumulationUnit})",
                        data =
                            listOf(
                                hourlyData.map { it.rainAccumulation },
                                hourlyData.map { it.snowAccumulation },
                            ),
                        seriesLabels = listOf("Rain", "Snow"),
                        labels = hourlyData.map { it.time },
                        colors =
                            listOf(
                                temp_low,
                                MaterialTheme.colorScheme.inverseSurface,
                            ),
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
                    color = temp_low,
                )

                WeatherMultiLineChart(
                    title = "Wind Speed & Gust (${weatherUiState.windUnit})",
                    data =
                        listOf(
                            hourlyData.map { it.windSpeed },
                            hourlyData.map { it.windGust },
                        ),
                    seriesLabels = listOf("Wind Speed", "Wind Gust"),
                    labels = hourlyData.map { it.time },
                    colors =
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary,
                        ),
                )

                WeatherChart(
                    title = "UV Index",
                    data = hourlyData.map { it.uvi },
                    labels = hourlyData.map { it.time },
                    color = MaterialTheme.colorScheme.error,
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
fun WeatherMultiBarChart(
    title: String,
    data: List<List<Float>>,
    seriesLabels: List<String>,
    labels: List<String>,
    colors: List<Color>,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnSeries {
                data.forEach { series(it) }
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
                                colors.map { color ->
                                    rememberLineComponent(
                                        color = color,
                                        thickness = 8.dp,
                                        shape = Shapes.roundedCornerShape(allPercent = 40),
                                    )
                                },
                        ),
                        startAxis = rememberStartAxis(),
                        bottomAxis =
                            rememberBottomAxis(
                                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2),
                                valueFormatter = { x, _, _ -> labels.getOrNull(x.toInt()) ?: "" },
                            ),
                        legend =
                            horizontalLegend(
                                items =
                                    seriesLabels.mapIndexed { index, label ->
                                        legendItem(
                                            icon = rememberShapeComponent(shape = Shapes.pillShape, color = colors[index]),
                                            label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface),
                                            labelText = label,
                                        )
                                    },
                                iconSize = 8.dp,
                                iconPadding = 4.dp,
                                spacing = 16.dp,
                                padding = dimensionsOf(top = 8.dp),
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
                                        backgroundShader = DynamicShaders.color(color.copy(alpha = 0.2f)),
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
fun WeatherMultiLineChart(
    title: String,
    data: List<List<Float>>,
    seriesLabels: List<String>,
    labels: List<String>,
    colors: List<Color>,
) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                data.forEach { series(it) }
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
                                colors.map { color ->
                                    LineCartesianLayer.LineSpec(
                                        shader = DynamicShaders.color(color),
                                        backgroundShader = DynamicShaders.color(color.copy(alpha = 0.2f)),
                                    )
                                },
                        ),
                        startAxis = rememberStartAxis(),
                        bottomAxis =
                            rememberBottomAxis(
                                itemPlacer = AxisItemPlacer.Horizontal.default(spacing = 2),
                                valueFormatter = { x, _, _ -> labels.getOrNull(x.toInt()) ?: "" },
                            ),
                        legend =
                            horizontalLegend(
                                items =
                                    seriesLabels.mapIndexed { index, label ->
                                        legendItem(
                                            icon = rememberShapeComponent(shape = Shapes.pillShape, color = colors[index]),
                                            label = rememberTextComponent(color = MaterialTheme.colorScheme.onSurface),
                                            labelText = label,
                                        )
                                    },
                                iconSize = 8.dp,
                                iconPadding = 4.dp,
                                spacing = 16.dp,
                                padding = dimensionsOf(top = 8.dp),
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}
