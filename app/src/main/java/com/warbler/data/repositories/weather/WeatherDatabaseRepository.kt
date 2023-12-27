package com.warbler.data.repositories.weather

import com.warbler.data.database.weather.WeatherDatabaseDao
import com.warbler.data.model.weather.WeatherDataEntity

class WeatherDatabaseRepository(private val weatherDatabaseDao: WeatherDatabaseDao) {
    fun insertWeather(weather: WeatherDataEntity) = weatherDatabaseDao.insertWeather(weather)
}
