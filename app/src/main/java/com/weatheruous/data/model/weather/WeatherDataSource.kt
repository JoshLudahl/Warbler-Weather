package com.weatheruous.data.model.weather

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class WeatherDataSource(
    @SerialName("current")
    val current: Current,
    @SerialName("daily")
    val daily: List<Daily>,
    @SerialName("hourly")
    val hourly: List<Hourly>,
    @SerialName("lat")
    val lat: Double,
    @SerialName("lon")
    val lon: Double,
    @SerialName("minutely")
    val minutely: List<Minutely>,
    @SerialName("timezone")
    val timezone: String,
    @SerialName("timezone_offset")
    val timezoneOffset: Int
)

@Keep
@Serializable
data class Current(
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("dt")
    val dt: Int,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("sunrise")
    val sunrise: Int,
    @SerialName("sunset")
    val sunset: Int,
    @SerialName("temp")
    val temp: Double,
    @SerialName("uvi")
    val uvi: Int,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("wind_deg")
    val windDeg: Int,
    @SerialName("wind_gust")
    val windGust: Double,
    @SerialName("wind_speed")
    val windSpeed: Double
)

@Keep
@Serializable
data class Daily(
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("dt")
    val dt: Int,
    @SerialName("feels_like")
    val feelsLike: FeelsLike,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("moon_phase")
    val moonPhase: Double,
    @SerialName("moonrise")
    val moonrise: Int,
    @SerialName("moonset")
    val moonset: Int,
    @SerialName("pop")
    val pop: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("rain")
    val rain: Double?,
    @SerialName("sunrise")
    val sunrise: Int,
    @SerialName("sunset")
    val sunset: Int,
    @SerialName("temp")
    val temp: Temp,
    @SerialName("uvi")
    val uvi: Double,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("wind_deg")
    val windDeg: Int,
    @SerialName("wind_gust")
    val windGust: Double,
    @SerialName("wind_speed")
    val windSpeed: Double
)

@Keep
@Serializable
data class Hourly(
    @SerialName("clouds")
    val clouds: Int,
    @SerialName("dew_point")
    val dewPoint: Double,
    @SerialName("dt")
    val dt: Int,
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("pop")
    val pop: Double,
    @SerialName("pressure")
    val pressure: Int,
    @SerialName("temp")
    val temp: Double,
    @SerialName("uvi")
    val uvi: Double,
    @SerialName("visibility")
    val visibility: Int,
    @SerialName("weather")
    val weather: List<Weather>,
    @SerialName("wind_deg")
    val windDeg: Int,
    @SerialName("wind_gust")
    val windGust: Double,
    @SerialName("wind_speed")
    val windSpeed: Double
)

@Keep
@Serializable
data class Minutely(
    @SerialName("dt")
    val dt: Int,
    @SerialName("precipitation")
    val precipitation: Int
)

@Keep
@Serializable
data class Weather(
    @SerialName("description")
    val description: String,
    @SerialName("icon")
    val icon: String,
    @SerialName("id")
    val id: Int,
    @SerialName("main")
    val main: String
)

@Keep
@Serializable
data class FeelsLike(
    @SerialName("day")
    val day: Double,
    @SerialName("eve")
    val eve: Double,
    @SerialName("morn")
    val morn: Double,
    @SerialName("night")
    val night: Double
)

@Keep
@Serializable
data class Temp(
    @SerialName("day")
    val day: Double,
    @SerialName("eve")
    val eve: Double,
    @SerialName("max")
    val max: Double,
    @SerialName("min")
    val min: Double,
    @SerialName("morn")
    val morn: Double,
    @SerialName("night")
    val night: Double
)
