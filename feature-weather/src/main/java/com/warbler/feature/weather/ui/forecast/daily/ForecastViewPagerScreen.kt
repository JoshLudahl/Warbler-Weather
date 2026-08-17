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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Navigation
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        com.warbler.feature.weather.ui.composables.DailyForecastCard(
            day = forecast.day,
            high = forecast.highTemp,
            low = forecast.lowTemp,
            icon = forecast.iconRes,
            modifier = Modifier.fillMaxWidth(),
        )

        if (forecast.summary.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = forecast.summary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val stats =
            buildList {
                add(
                    WeatherStat(
                        title = "Sunrise",
                        value = forecast.sunrise,
                        description = "Sunrise",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_sunrise, contentDescription = "Sunrise")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Sunset",
                        value = forecast.sunset,
                        description = "Sunset",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_sunset, contentDescription = "Sunset")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Moonrise",
                        value = forecast.moonrise,
                        description = "Moonrise",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_moonrise, contentDescription = "Moonrise")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Moonset",
                        value = forecast.moonset,
                        description = "Moonset",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_moonset, contentDescription = "Moonset")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Humidity",
                        value = forecast.humidity,
                        description = "Humidity",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_humidity, contentDescription = "Humidity")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "UV Index",
                        value = forecast.uvIndex,
                        description = "UV Index",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_day_sunny, contentDescription = "UV Index")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Pressure",
                        value = forecast.pressure,
                        description = "Pressure",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_barometer, contentDescription = "Pressure")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Wind",
                        value = forecast.wind,
                        description = "Wind Speed",
                        icon = {
                            StatIcon(
                                icon = Icons.Rounded.Navigation,
                                contentDescription = "Wind Speed",
                                rotation = forecast.windDeg.toFloat(),
                            )
                        },
                    ),
                )

                add(
                    WeatherStat(
                        title = "Rain",
                        value = forecast.rain,
                        description = "Rain",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_umbrella, contentDescription = "Rain")
                        },
                    ),
                )

                add(
                    WeatherStat(
                        title = "Clouds",
                        value = forecast.clouds,
                        description = "Clouds",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_cloud, contentDescription = "Clouds")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Dew Point",
                        value = forecast.dewPoint,
                        description = "Dew Point",
                        icon = {
                            StatIcon(icon = R.drawable.ic_wi_raindrops, contentDescription = "Dew Point")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Precipitation",
                        value = forecast.pop,
                        description = "Precipitation Probability",
                        icon = {
                            StatIcon(
                                icon = R.drawable.ic_wi_raindrops,
                                contentDescription = "Precipitation",
                            )
                        },
                    ),
                )

                if (forecast.snow.any { it.isDigit() && it != '0' }) {
                    add(
                        WeatherStat(
                            title = "Snow",
                            value = forecast.snow,
                            description = "Snow",
                            icon = {
                                StatIcon(icon = R.drawable.ic_13d, contentDescription = "Snow")
                            },
                        ),
                    )
                }

                add(
                    WeatherStat(
                        title = "Wind Gust",
                        value = forecast.windGust,
                        description = "Wind Gust Speed",
                        icon = {
                            StatIcon(
                                icon = R.drawable.ic_wi_strong_wind,
                                contentDescription = "Wind Gust Speed",
                            )
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Moon Phase",
                        value = forecast.moonPhaseName,
                        description = "Moon Phase",
                        icon = {
                            StatIcon(icon = R.drawable.ic_01n, contentDescription = "Moon Phase")
                        },
                    ),
                )
                add(
                    WeatherStat(
                        title = "Feels Like",
                        value = forecast.feelsLikeDay,
                        description = "Feels Like (Day)",
                        icon = {
                            StatIcon(
                                icon = R.drawable.ic_wi_day_sunny,
                                contentDescription = "Feels Like",
                            )
                        },
                    ),
                )
            }

        WeatherStatsGrid(stats = stats)
    }
}

@Composable
private fun StatIcon(
    icon: Any,
    contentDescription: String,
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
) {
    when (icon) {
        is ImageVector -> {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier =
                    modifier
                        .size(38.dp)
                        .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        is Int -> {
            Image(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                modifier =
                    modifier
                        .size(48.dp)
                        .rotate(rotation),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
        }
    }
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
                        moonrise = "9:00 PM",
                        moonset = "5:00 AM",
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
