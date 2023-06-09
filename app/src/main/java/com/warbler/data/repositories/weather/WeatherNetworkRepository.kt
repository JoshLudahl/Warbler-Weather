package com.warbler.data.repositories.weather

import android.util.Log
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.network.weather.WeatherApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherNetworkRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {

    fun getCurrentWeather(location: LocationEntity): Flow<WeatherDataSource> = flow {
        Log.d("WeatherNetworkRepository", "Making request to API service to fetch weather.")
        val weather = weatherApiService.getWeather(location.lat, location.lon)
        Log.d("WeatherNetworkRepository", "getCurrentWeather: $weather")
        emit(weather)
    }.flowOn(Dispatchers.IO)
}
