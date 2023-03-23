package com.weatheruous.data.model.weather

object Conversion {

    val Double.toCelsiusFromKelvin: Double
        get() = this - 273.15

    val Double.toFahrenheitFromKelvin: Double
        get() = (this - 273.15) * 9 / 5 + 32
}
