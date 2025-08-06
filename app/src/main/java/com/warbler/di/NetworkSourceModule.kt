package com.warbler.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.warbler.data.network.NetworkConstants
import com.warbler.data.network.NetworkInterceptor
import com.warbler.data.network.locations.LocationApiService
import com.warbler.data.network.weather.WeatherApiService
import com.warbler.data.repositories.location.LocationNetworkRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkSourceModule {
    @Singleton
    @Provides
    fun providesHttpClientWithInterceptor(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient
            .Builder()
            .addInterceptor(NetworkInterceptor(context))
            .addInterceptor(interceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofitService(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }

        return Retrofit
            .Builder()
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType = contentType))
            .baseUrl(NetworkConstants.WEATHER_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun providesWeatherAPIService(retrofit: Retrofit): WeatherApiService = retrofit.create(WeatherApiService::class.java)

    @Singleton
    @Provides
    fun providesLocationAPIService(retrofit: Retrofit): LocationApiService = retrofit.create(LocationApiService::class.java)

    @Singleton
    @Provides
    fun providesWeatherNetworkRepository(weatherApiService: WeatherApiService) = WeatherNetworkRepository(weatherApiService)

    @Singleton
    @Provides
    fun providesLocationNetworkRepository(locationApiService: LocationApiService) = LocationNetworkRepository(locationApiService)
}
