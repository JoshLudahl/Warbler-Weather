package com.weatheruous.data.repositories.weather

import com.weatheruous.data.model.location.LocationItem
import com.weatheruous.data.network.weather.WeatherApiService

class WeatherNetworkRepository(private val weatherApiService: WeatherApiService) {

    fun getCurrentWeather(location: LocationItem) =
        weatherApiService.getWeather(location.lat, location.lon)
}
