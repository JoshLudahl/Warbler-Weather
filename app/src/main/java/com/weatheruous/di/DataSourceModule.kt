package com.weatheruous.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.weatheruous.data.database.location.LocationDao
import com.weatheruous.data.database.weather.WeatherDatabase
import com.weatheruous.data.database.weather.WeatherDatabaseDao
import com.weatheruous.data.repositories.location.LocationRepository
import com.weatheruous.data.repositories.weather.WeatherDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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

    @Singleton
    @Provides
    fun providesLocationDatabaseDao(database: WeatherDatabase) = database.locationDao()

    @Singleton
    @Provides
    fun providesLocationRepository(locationDao: LocationDao) = LocationRepository(locationDao)

    @Singleton
    @Provides
    fun providesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
