package com.weatheruous.data.repositories.weather

import android.util.Log
import com.weatheruous.data.model.location.LocationEntity
import com.weatheruous.data.model.weather.WeatherDataSource
import com.weatheruous.data.network.weather.WeatherApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WeatherNetworkRepository @Inject constructor(
    private val weatherApiService: WeatherApiService
) {

    fun getCurrentWeather(location: LocationEntity): Flow<WeatherDataSource> = flow {
        val weather = weatherApiService.getWeather(location.lat, location.lon)
        Log.d("WeatherNetworkRepository", "getCurrentWeather: $weather")
        emit(weather)
    }.flowOn(Dispatchers.IO)
}
