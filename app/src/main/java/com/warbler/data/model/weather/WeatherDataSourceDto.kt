package com.warbler.data.model.weather

import com.patrykandpatrick.vico.core.extension.mutableListOf
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
}
