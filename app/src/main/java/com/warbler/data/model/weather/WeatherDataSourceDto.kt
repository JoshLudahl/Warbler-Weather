package com.warbler.data.model.weather

import android.content.Context
import com.patrykandpatrick.vico.core.extension.mutableListOf
import com.warbler.R
import com.warbler.data.model.weather.Conversion.fromDoubleToPercentage
import com.warbler.data.model.weather.Conversion.fromHourWithSuffix
import com.warbler.data.model.weather.Conversion.getDatOfWeekFromUnixUTC
import com.warbler.data.model.weather.Conversion.toDegrees
import com.warbler.data.model.weather.Conversion.toReadableHour
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.ui.settings.Temperature
import kotlin.math.roundToInt

object WeatherDataSourceDto {
    fun buildWeatherData(weatherDataSource: WeatherDataSource): WeatherDataEntity {
        return WeatherDataEntity(
            city = getCityNameFromLatLon(weatherDataSource.lat, weatherDataSource.lon),
            condition = weatherDataSource.current.weather[0].main,
            description = weatherDataSource.current.weather[0].description,
            hi = weatherDataSource.daily[0].temp.max,
            lat = weatherDataSource.lat,
            lon = weatherDataSource.lon,
            low = weatherDataSource.daily[0].temp.min,
            pressure = weatherDataSource.current.pressure,
            temp = weatherDataSource.current.temp,
            wind = weatherDataSource.current.windSpeed,
        )
    }

    private fun getCityNameFromLatLon(
        lat: Double,
        lon: Double,
    ): String {
        // TODO: Build out location service
        return ""
    }

    fun buildWeatherForecast(
        weatherDataSource: WeatherDataSource,
        units: Temperature,
    ): List<WeatherForecast> {
        return weatherDataSource.daily.mapIndexed { index, daily ->
            WeatherForecast(
                dayOfWeek = getDatOfWeekFromUnixUTC(daily.dt.toLong()),
                hi =
                    Conversion.fromKelvinToProvidedUnit(daily.temp.max, units).roundToInt()
                        .toString(),
                index = index,
                low =
                    Conversion.fromKelvinToProvidedUnit(daily.temp.min, units)
                        .roundToInt().toString(),
                icon = daily.weather[0].icon.getIconForCondition,
            )
        }
    }

    fun buildHourlyForecastList(
        weatherDataSource: WeatherDataSource,
        units: Temperature,
    ): List<HourlyForecastItem> {
        val list = mutableListOf<HourlyForecastItem>()

        weatherDataSource.hourly.forEach { hour ->

            val computedHour = hour.dt + weatherDataSource.timezoneOffset
            val temperature =
                Conversion.fromKelvinToProvidedUnit(hour.temp, units)
                    .roundToInt()
                    .toDegrees

            list.add(
                HourlyForecastItem(
                    hour = computedHour.toReadableHour.fromHourWithSuffix,
                    description = hour.weather[0].description,
                    formattedTemp = temperature,
                    icon = hour.weather[0].icon.getIconForCondition,
                ),
            )
        }

        return list
    }

    fun buildWeatherDetailList(
        context: Context,
        forecast: Forecast,
    ): List<WeatherDetailItem> {
        val list = ArrayList<WeatherDetailItem>()

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunrise,
                value =
                    Conversion.getTimeFromTimeStamp(
                        timeStamp = forecast.daily.sunrise.toLong(),
                        offset = forecast.timeZoneOffset.toLong(),
                    ) + " AM",
                label = R.string.sunrise,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_sunset,
                value =
                    Conversion.getTimeFromTimeStamp(
                        timeStamp = forecast.daily.sunset.toLong(),
                        offset = forecast.timeZoneOffset.toLong(),
                    ) + " PM",
                label = R.string.sunset,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_raindrops,
                value =
                    Conversion.fromKelvinToProvidedUnit(
                        value = forecast.daily.dewPoint,
                        unit = forecast.temperature,
                    ).toInt().toDegrees,
                label = R.string.dew_point,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_cloud,
                value = context.getString(R.string.cloudy, forecast.daily.clouds.toString()),
                label = R.string.clouds,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_umbrella,
                value =
                    context.getString(
                        R.string.percentage,
                        "${forecast.daily.pop.fromDoubleToPercentage}",
                    ),
                label = R.string.chance_of_rain,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_humidity,
                value =
                    context.getString(
                        R.string.percentage,
                        "${forecast.daily.humidity}",
                    ),
                label = R.string.humidity,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_day_sunny,
                value = forecast.daily.uvi.toString(),
                label = R.string.uv_index,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_barometer,
                value = context.getString(R.string.pressure, "${forecast.daily.pressure}"),
                label = R.string.pressure_text,
            ),
        )

        list.add(
            WeatherDetailItem(
                icon = R.drawable.ic_wi_strong_wind,
                value =
                    Conversion.formatSpeedUnitsWithUnitsToString(
                        value = forecast.daily.windSpeed ?: 0.00,
                        speed = forecast.speed,
                    ),
                label = R.string.wind,
            ),
        )

        return list
    }
}
