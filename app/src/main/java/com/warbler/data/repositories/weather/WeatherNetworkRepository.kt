package com.warbler.data.repositories.weather

import android.util.Log
import com.warbler.data.model.location.LocationEntity
import com.warbler.data.model.weather.AirQualitySource
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.network.weather.WeatherApiService
import com.warbler.utilities.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WeatherNetworkRepository
    @Inject
    constructor(
        private val weatherApiService: WeatherApiService,
    ) {
        fun getCurrentWeather(location: LocationEntity): Flow<WeatherDataSource> =
            flow {
                Log.d("WeatherNetworkRepository", "Making request to API service to fetch weather.")
                val weather = weatherApiService.getWeather(location.lat, location.lon)
                Log.d("WeatherNetworkRepository", "getCurrentWeather: $weather")
                emit(weather)
            }.flowOn(Dispatchers.IO)

        fun getCurrentAqi(location: LocationEntity): Flow<Resource<AirQualitySource>> =
            flow {
                emit(Resource.Loading)
                Log.d("WeatherNetworkRepository", "Making request to API service to fetch AQI.")
                try {
                    val aqi = weatherApiService.getAirPollution(location.lat, location.lon)
                    Log.d("WeatherNetworkRepository", "getCurrentAqi: $aqi")
                    emit(Resource.Success(aqi))
                } catch (e: Exception) {
                    Log.d("WeatherNetworkRepository", "getCurrentAqi: $e")
                    emit(Resource.Error(exception = e, message = e.message))
                }
            }.flowOn(Dispatchers.IO)
    }
