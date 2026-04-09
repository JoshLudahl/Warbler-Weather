package com.warbler.config

import com.warbler.data.model.location.LocationDataSource
import com.warbler.data.model.weather.AirQualitySource
import com.warbler.data.model.weather.Aqi
import com.warbler.data.model.weather.Components
import com.warbler.data.model.weather.Coord
import com.warbler.data.model.weather.Current
import com.warbler.data.model.weather.Daily
import com.warbler.data.model.weather.FeelsLike
import com.warbler.data.model.weather.Hourly
import com.warbler.data.model.weather.Main
import com.warbler.data.model.weather.Temp
import com.warbler.data.model.weather.Weather
import com.warbler.data.model.weather.WeatherDataSource
import com.warbler.data.network.locations.LocationApiService
import com.warbler.data.network.weather.WeatherApiService
import com.warbler.data.repositories.location.LocationNetworkRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.di.NetworkSourceModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [NetworkSourceModule::class],
)
object FakeNetworkModule {
    private val dummyWeather =
        Weather(
            description = "clear sky",
            icon = "01d",
            id = 800,
            main = "Clear",
        )

    private val dummyCurrent =
        Current(
            clouds = 0,
            dewPoint = 280.0,
            dt = 1700000000,
            feelsLike = 295.0,
            humidity = 50,
            pressure = 1013,
            sunrise = 1699990000,
            sunset = 1700030000,
            temp = 296.0,
            uvi = 5.0,
            visibility = 10000,
            weather = listOf(dummyWeather),
            windSpeed = 3.0,
        )

    private val dummyDaily =
        Daily(
            clouds = 10,
            dewPoint = 280.0,
            dt = 1700000000,
            feelsLike = FeelsLike(day = 295.0, eve = 293.0, morn = 290.0, night = 288.0),
            humidity = 50,
            moonPhase = 0.5,
            moonrise = 1700000000,
            moonset = 1700040000,
            pop = 0.1,
            pressure = 1013,
            summary = "Clear skies throughout the day",
            sunrise = 1699990000,
            sunset = 1700030000,
            temp = Temp(day = 296.0, eve = 294.0, max = 298.0, min = 288.0, morn = 290.0, night = 289.0),
            uvi = 5.0,
            weather = listOf(dummyWeather),
        )

    private val dummyHourly =
        Hourly(
            clouds = 10,
            dewPoint = 280.0,
            dt = 1700000000,
            feelsLike = 295.0,
            humidity = 50,
            pop = 0.1,
            pressure = 1013,
            temp = 10.0,
            uvi = 5.0,
            weather = listOf(dummyWeather),
            windDeg = 180,
            windGust = 5.0,
            windSpeed = 3.0,
        )

    private val dummyWeatherDataSource =
        WeatherDataSource(
            current = dummyCurrent,
            daily = List(3) { dummyDaily },
            hourly = listOf(dummyHourly),
            lat = 40.7128,
            lon = -74.0060,
            timezone = "America/New_York",
            timezoneOffset = -18000,
        )

    private val dummyAirQualitySource =
        AirQualitySource(
            coord = Coord(lat = 40.7128, lon = -74.0060),
            list =
                listOf(
                    Aqi(
                        components =
                            Components(
                                co = 200.0,
                                nh3 = 1.0,
                                no = 0.5,
                                no2 = 10.0,
                                o3 = 60.0,
                                pm10 = 15.0,
                                pm25 = 8.0,
                                so2 = 5.0,
                            ),
                        dt = 1700000000L,
                        main = Main(aqi = 1),
                    ),
                ),
        )

    private val dummyLocationDataSource =
        LocationDataSource(
            country = "US",
            lat = 40.7128,
            lon = -74.0060,
            name = "New York",
            state = "New York",
        )

    @Singleton
    @Provides
    fun providesWeatherAPIService(): WeatherApiService =
        object : WeatherApiService {
            override suspend fun getWeather(
                lat: Double,
                lon: Double,
                apiKey: String,
            ): WeatherDataSource = dummyWeatherDataSource

            override suspend fun getAirPollution(
                lat: Double,
                lon: Double,
                apiKey: String,
            ): AirQualitySource = dummyAirQualitySource
        }

    @Singleton
    @Provides
    fun providesLocationAPIService(): LocationApiService =
        object : LocationApiService {
            override suspend fun getLocations(
                query: String,
                limit: Int,
                apiKey: String,
            ): List<LocationDataSource> = listOf(dummyLocationDataSource)

            override suspend fun reverseGeocode(
                latitude: Double,
                longitude: Double,
                limit: Int,
                apiKey: String,
            ): List<LocationDataSource> = listOf(dummyLocationDataSource)
        }

    @Singleton
    @Provides
    fun providesWeatherNetworkRepository(weatherApiService: WeatherApiService): WeatherNetworkRepository = WeatherNetworkRepository(weatherApiService)

    @Singleton
    @Provides
    fun providesLocationNetworkRepository(locationApiService: LocationApiService): LocationNetworkRepository = LocationNetworkRepository(locationApiService)
}
