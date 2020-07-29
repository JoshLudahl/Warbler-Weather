package com.weatheruous.model

data class WeatherData(
    var properties: Properties
)

data class Properties(
    val updated: String,
    val units: String,
    val updateTime: String,
    val elevation: Elevation,
    val periods: List<Periods>
)

data class Elevation(
    val value: Float,
    val unitCode: String
)

data class Periods(
    val number: String,
    val name: String,
    val startTime: String,
    val endTime: String,
    val isDaytime: Boolean,
    val temperature: Int,
    val temperatureUnit: String,
    val temperatureTrend: String?,
    val windSpeed: String,
    val windDirection: String,
    val icon: String,
    val shortForecast: String,
    val detailedForecast: String
)

data class Locations(
    val properties: WeatherLocationProperties
)

data class WeatherLocationProperties(
    val forcast: String,
    val forcastHourly: String,
    val relativeLocation: RelativeLocation
)

data class RelativeLocation(
    val properties: LocationProperties
)

data class LocationProperties(
    val city: String,
    val state: String
)