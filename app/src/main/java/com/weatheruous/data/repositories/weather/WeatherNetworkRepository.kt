package com.weatheruous.data.repositories.weather

import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.network.Resource
import com.weatheruous.data.network.weather.WeatherApiService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherNetworkRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {

    fun getCurrentWeather(location: LocationEntity): Flow<Resource> = flow {
        weatherApiService.getWeather(location.lat, location.lon)
    }
}
