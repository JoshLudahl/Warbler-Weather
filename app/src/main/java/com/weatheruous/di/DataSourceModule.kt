package com.weatheruous.di

import android.content.Context
import androidx.room.Room
import com.weatheruous.data.database.weather.WeatherDatabase
import com.weatheruous.data.database.weather.WeatherDatabaseDao
import com.weatheruous.data.repositories.WeatherDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun providesWeatherDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        WeatherDatabase::class.java,
        "weather_database"
    ).build()

    @Singleton
    @Provides
    fun providesWeatherDatabaseDao(database: WeatherDatabase) = database.weatherDao()

    @Singleton
    @Provides
    fun providesWeatherDatabaseRepository(weatherDatabaseDao: WeatherDatabaseDao) =
        WeatherDatabaseRepository(weatherDatabaseDao)
}
