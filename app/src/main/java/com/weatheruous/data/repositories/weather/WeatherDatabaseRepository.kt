package com.weatheruous.data.repositories.weather

import com.weatheruous.data.database.weather.WeatherDatabaseDao
import com.weatheruous.data.model.weather.WeatherDataEntity

class WeatherDatabaseRepository(private val weatherDatabaseDao: WeatherDatabaseDao) {

    fun insertWeather(weather: WeatherDataEntity) = weatherDatabaseDao.insertWeather(weather)
}
