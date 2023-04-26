package com.warbler.data.model.weather

import com.warbler.data.model.weather.Conversion.getDatOfWeekFromUnixUTC
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
            wind = weatherDataSource.current.windSpeed
        )
    }

    private fun getCityNameFromLatLon(lat: Double, lon: Double): String {
        // TODO: Build out location service
        return ""
    }

    fun buildWeatherForecast(weatherDataSource: WeatherDataSource, units: Temperature):
        List<WeatherForecast> {
        return weatherDataSource.daily.map {
            WeatherForecast(
                dayOfWeek = getDatOfWeekFromUnixUTC(it.dt.toLong()),
                hi = Conversion.fromKelvinToProvidedUnit(it.temp.max, units).roundToInt()
                    .toString(),
                low = Conversion.fromKelvinToProvidedUnit(it.temp.min, units)
                    .roundToInt().toString(),
                icon = it.weather[0].icon.getIconForCondition
            )
        }
    }
}
