package com.weatheruous.data.model.weather

class WeatherDataSourceDto {
    fun buildWeatherData(weatherDataSource: WeatherDataSource): WeatherData {
        return WeatherData(
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
}
