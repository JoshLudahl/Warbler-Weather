package com.weatheruous.di

import com.weatheruous.data.network.locations.LocationAPI
import com.weatheruous.data.network.weather.WeatherAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkSourceModule {

    @Singleton
    @Provides
    fun providesWeatherApiService() = WeatherAPI.weatherApiService

    @Singleton
    @Provides
    fun providesLocationApiService() = LocationAPI.locationApiService
}
