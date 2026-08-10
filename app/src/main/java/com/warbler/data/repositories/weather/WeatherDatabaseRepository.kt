package com.warbler.data.repositories.weather

import com.warbler.data.database.weather.WeatherDatabaseDao
import com.warbler.data.model.weather.WeatherCacheEntity
import com.warbler.data.model.weather.WeatherDataEntity
import kotlinx.coroutines.flow.Flow

class WeatherDatabaseRepository(
    private val weatherDatabaseDao: WeatherDatabaseDao,
) {
    suspend fun insertWeather(weather: WeatherDataEntity) = weatherDatabaseDao.insertWeather(weather)

    suspend fun insertWeatherCache(cache: WeatherCacheEntity) = weatherDatabaseDao.insertWeatherCache(cache)

    fun getCurrentWeather(): Flow<WeatherDataEntity?> = weatherDatabaseDao.getCurrentWeather()

    fun getWeatherByLocation(
        lat: Double,
        lon: Double,
    ): Flow<WeatherDataEntity?> = weatherDatabaseDao.getWeatherByLocation(lat, lon)

    suspend fun getCurrentWeatherSync(): WeatherDataEntity? = weatherDatabaseDao.getCurrentWeatherSync()

    suspend fun getWeatherCache(
        lat: Double,
        lon: Double,
    ): WeatherCacheEntity? = weatherDatabaseDao.getWeatherCache(lat, lon)
}
