package com.warbler.feature.weather.ui.forecast.daily

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.warbler.core.model.appearance.ThemeMode
import com.warbler.core.model.appearance.ThemeStyle
import com.warbler.core.theme.AppTheme
import com.warbler.core.utilities.ShareUtils
import com.warbler.feature.weather.R
import com.warbler.feature.weather.ui.composables.WeatherStat
import com.warbler.feature.weather.ui.composables.WeatherStatsGrid
import com.warbler.feature.weather.ui.main.DailyForecastItem
import com.warbler.feature.weather.ui.main.WeatherUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastViewPagerScreen(
    weatherUiState: WeatherUiState?,
    initialPage: Int,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    val dailyForecasts = weatherUiState?.dailyForecasts ?: emptyList()
    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { dailyForecasts.size },
        )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text =
                            if (dailyForecasts.isNotEmpty()) {
                                dailyForecasts[pagerState.currentPage].dateTitle
                            } else {
                                "Forecast Details"
                            },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (dailyForecasts.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                    ShareUtils.shareImage(context, bitmap)
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Share Forecast",
                            )
                        }
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
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                val forecast = dailyForecasts[page]
                ForecastDetailContent(forecast)
            }

            if (dailyForecasts.size > 1) {
                PagerIndicator(
                    pageCount = dailyForecasts.size,
                    currentPage = pagerState.currentPage,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { iteration ->
            val color =
                if (currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            Box(
                modifier =
                    Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp),
            )
        }
    }
}

@Composable
private fun ForecastDetailContent(forecast: DailyForecastItem) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        com.warbler.feature.weather.ui.composables.DailyForecastCard(
            day = forecast.day,
            high = forecast.highTemp,
            low = forecast.lowTemp,
            icon = forecast.iconRes,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        val stats =
            listOf(
                WeatherStat(
                    title = "Sunrise",
                    value = forecast.sunrise,
                    description = "Sunrise",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_sunrise, contentDescription = "Sunrise")
                    },
                ),
                WeatherStat(
                    title = "Sunset",
                    value = forecast.sunset,
                    description = "Sunset",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_sunset, contentDescription = "Sunset")
                    },
                ),
                WeatherStat(
                    title = "Humidity",
                    value = forecast.humidity,
                    description = "Humidity",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_humidity, contentDescription = "Humidity")
                    },
                ),
                WeatherStat(
                    title = "UV Index",
                    value = forecast.uvIndex,
                    description = "UV Index",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_day_sunny, contentDescription = "UV Index")
                    },
                ),
                WeatherStat(
                    title = "Pressure",
                    value = forecast.pressure,
                    description = "Pressure",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_barometer, contentDescription = "Pressure")
                    },
                ),
                WeatherStat(
                    title = "Wind",
                    value = forecast.wind,
                    description = "Wind Speed",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wind, contentDescription = "Wind Speed")
                    },
                ),
                WeatherStat(
                    title = "Rain",
                    value = forecast.rain,
                    description = "Rain",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_umbrella, contentDescription = "Rain")
                    },
                ),
                WeatherStat(
                    title = "Clouds",
                    value = forecast.clouds,
                    description = "Clouds",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_cloud, contentDescription = "Clouds")
                    },
                ),
                WeatherStat(
                    title = "Dew Point",
                    value = forecast.dewPoint,
                    description = "Dew Point",
                    icon = {
                        StatIcon(icon = R.drawable.ic_wi_raindrops, contentDescription = "Dew Point")
                    },
                ),
            )

        WeatherStatsGrid(stats = stats)
    }
}

@Composable
private fun StatIcon(
    icon: Int,
    contentDescription: String,
) {
    Image(
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        modifier = Modifier.size(48.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
    )
}

@Preview(showBackground = true)
@Composable
private fun ForecastViewPagerScreenPreview() {
    val mockUiState =
        WeatherUiState(
            locationName = "New York",
            temperature = "72°F",
            description = "Sunny",
            dateTitle = "Monday, August 10",
            feelsLike = "Feels like 75°F",
            hasAqi = true,
            aqiValue = "1",
            hasAlerts = false,
            iconRes = R.drawable.ic_wi_day_sunny,
            dailyForecasts =
                listOf(
                    DailyForecastItem(
                        day = "Today",
                        dateTitle = "Monday, August 10",
                        highTemp = 80,
                        lowTemp = 65,
                        iconRes = R.drawable.ic_wi_day_sunny,
                        description = "Sunny",
                        sunrise = "6:00 AM",
                        sunset = "8:00 PM",
                        humidity = "50%",
                        wind = "10 MPH",
                        uvIndex = "5",
                        pressure = "1012 hPa",
                        dewPoint = "60°F",
                    ),
                ),
        )
    AppTheme(themeMode = ThemeMode.LIGHT, themeStyle = ThemeStyle.DEFAULT) {
        ForecastViewPagerScreen(
            weatherUiState = mockUiState,
            initialPage = 0,
            onNavigateUp = {},
        )
    }
}
