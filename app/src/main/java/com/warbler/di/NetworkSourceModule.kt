package com.warbler.di

import com.warbler.data.network.locations.LocationAPI
import com.warbler.data.network.weather.WeatherAPI
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
