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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LegendItem
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.warbler.core.theme.temp_low
import com.warbler.data.model.weather.Conversion
import com.warbler.feature.weather.ui.composables.HourlyForecastSection
import com.warbler.feature.weather.ui.main.WeatherUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
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
        if (weatherUiState != null) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
            ) {
                Log.d("HourlyScreen", "weatherUiState: $weatherUiState")
                Spacer(modifier = Modifier.height(16.dp))
                HourlyForecastSection(weatherUiState = weatherUiState)

                val hourlyData = weatherUiState.hourlyForecasts

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
                    valueFormatter = Conversion.wholeNumberValueFormatter,
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
                            MaterialTheme.colorScheme.outline,
                            MaterialTheme.colorScheme.inversePrimary,
                        ),
                    valueFormatter = Conversion.wholeNumberValueFormatter,
                )

                WeatherChart(
                    title = "UV Index",
                    data = hourlyData.map { it.uvi },
                    labels = hourlyData.map { it.time },
                    color = MaterialTheme.colorScheme.error,
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularWavyProgressIndicator()
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
                contentAlignment = Alignment.Center,
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
    valueFormatter: CartesianValueFormatter? = null,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            columnModel {
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

        val legendIcons =
            colors.map { color ->
                rememberLineComponent(fill = Fill(color), shape = CircleShape)
            }
        val legendLabelComponent =
            rememberTextComponent(
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
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
                            columnProvider =
                                ColumnCartesianLayer.ColumnProvider.series(
                                    colors.map { color ->
                                        rememberLineComponent(
                                            fill = Fill(color),
                                            thickness = 8.dp,
                                            shape = RoundedCornerShape(percent = 40),
                                        )
                                    },
                                ),
                        ),
                        startAxis =
                            VerticalAxis.rememberStart(
                                label = rememberAxisLabelComponent(margins = Insets(end = 8.dp)),
                                valueFormatter = valueFormatter ?: CartesianValueFormatter.decimal(),
                            ),
                        bottomAxis =
                            HorizontalAxis.rememberBottom(
                                label = rememberAxisLabelComponent(margins = Insets(top = 8.dp)),
                                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = { 2 }),
                                valueFormatter =
                                    CartesianValueFormatter { _, value, _ ->
                                        labels.getOrNull(value.toInt()) ?: ""
                                    },
                            ),
                        legend =
                            rememberHorizontalLegend(
                                items = {
                                    seriesLabels.forEachIndexed { index, label ->
                                        add(
                                            LegendItem(
                                                icon = legendIcons[index],
                                                labelComponent = legendLabelComponent,
                                                label = label,
                                            ),
                                        )
                                    }
                                },
                                iconSize = 8.dp,
                                iconLabelSpacing = 4.dp,
                                columnSpacing = 16.dp,
                                padding = Insets(top = 8.dp),
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(16.dp),
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
    valueFormatter: CartesianValueFormatter? = null,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineModel {
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
                            lineProvider =
                                LineCartesianLayer.LineProvider.series(
                                    LineCartesianLayer.rememberLine(
                                        fill = LineCartesianLayer.LineFill.single(Fill(color)),
                                        areaFill =
                                            LineCartesianLayer.AreaFill.single(
                                                Fill(color.copy(alpha = 0.2f)),
                                            ),
                                    ),
                                ),
                        ),
                        startAxis =
                            VerticalAxis.rememberStart(
                                label = rememberAxisLabelComponent(margins = Insets(end = 8.dp)),
                                valueFormatter = valueFormatter ?: CartesianValueFormatter.decimal(),
                            ),
                        bottomAxis =
                            HorizontalAxis.rememberBottom(
                                label = rememberAxisLabelComponent(margins = Insets(top = 8.dp)),
                                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = { 2 }),
                                valueFormatter =
                                    CartesianValueFormatter { _, value, _ ->
                                        labels.getOrNull(value.toInt()) ?: ""
                                    },
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(16.dp),
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
    valueFormatter: CartesianValueFormatter? = null,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineModel {
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

        val legendIcons =
            colors.map { color ->
                rememberLineComponent(fill = Fill(color), shape = CircleShape)
            }
        val legendLabelComponent =
            rememberTextComponent(
                style = TextStyle(color = MaterialTheme.colorScheme.onSurface),
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
                            lineProvider =
                                LineCartesianLayer.LineProvider.series(
                                    colors.map { color ->
                                        LineCartesianLayer.rememberLine(
                                            fill = LineCartesianLayer.LineFill.single(Fill(color)),
                                            areaFill =
                                                LineCartesianLayer.AreaFill.single(
                                                    Fill(color.copy(alpha = 0.2f)),
                                                ),
                                        )
                                    },
                                ),
                        ),
                        startAxis =
                            VerticalAxis.rememberStart(
                                label = rememberAxisLabelComponent(margins = Insets(end = 8.dp)),
                                valueFormatter = valueFormatter ?: CartesianValueFormatter.decimal(),
                            ),
                        bottomAxis =
                            HorizontalAxis.rememberBottom(
                                label = rememberAxisLabelComponent(margins = Insets(top = 8.dp)),
                                itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = { 2 }),
                                valueFormatter =
                                    CartesianValueFormatter { _, value, _ ->
                                        labels.getOrNull(value.toInt()) ?: ""
                                    },
                            ),
                        legend =
                            rememberHorizontalLegend(
                                items = {
                                    seriesLabels.forEachIndexed { index, label ->
                                        add(
                                            LegendItem(
                                                icon = legendIcons[index],
                                                labelComponent = legendLabelComponent,
                                                label = label,
                                            ),
                                        )
                                    }
                                },
                                iconSize = 8.dp,
                                iconLabelSpacing = 4.dp,
                                columnSpacing = 16.dp,
                                padding = Insets(top = 8.dp),
                            ),
                    ),
                modelProducer = modelProducer,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
