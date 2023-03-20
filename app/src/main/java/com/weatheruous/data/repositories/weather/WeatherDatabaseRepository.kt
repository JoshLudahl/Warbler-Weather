package com.weatheruous.data.repositories.weather

import com.weatheruous.data.database.weather.WeatherDatabaseDao
import com.weatheruous.data.model.weather.WeatherData

class WeatherDatabaseRepository(private val weatherDatabaseDao: WeatherDatabaseDao) {

    fun insertWeather(weather: WeatherData) = weatherDatabaseDao.insertWeather(weather)
}
