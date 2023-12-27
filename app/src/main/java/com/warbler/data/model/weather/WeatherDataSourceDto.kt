package com.warbler.data.model.weather

import com.patrykandpatrick.vico.core.extension.mutableListOf
import com.warbler.data.model.weather.Conversion.getDatOfWeekFromUnixUTC
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.ui.settings.Temperature
import java.time.Instant
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
            wind = weatherDataSource.current.windSpeed
        )
    }

    private fun getCityNameFromLatLon(lat: Double, lon: Double): String {
        // TODO: Build out location service
        return ""
    }

    fun buildWeatherForecast(weatherDataSource: WeatherDataSource, units: Temperature):
        List<WeatherForecast> {
        return weatherDataSource.daily.mapIndexed { index, daily ->
            WeatherForecast(
                dayOfWeek = getDatOfWeekFromUnixUTC(daily.dt.toLong()),
                hi = Conversion.fromKelvinToProvidedUnit(daily.temp.max, units).roundToInt()
                    .toString(),
                index = index,
                low = Conversion.fromKelvinToProvidedUnit(daily.temp.min, units)
                    .roundToInt().toString(),
                icon = daily.weather[0].icon.getIconForCondition
            )
        }
    }

    fun buildHourlyRainMap(weather: WeatherDataSource): List<Pair<Long, Float>> {
        val list = mutableListOf<Pair<Long, Float>>()
        weather.hourly.forEach { hour ->
            val time = Instant.ofEpochSecond(
                (hour.dt + weather.timezoneOffset)
                    .toLong()
            )
                .toEpochMilli()

            val rain = hour.rain?.h?.toFloat() ?: 0f

            list.add(time to rain)
        }

        return list
    }
}
