package com.warbler.data.model.weather

import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

object Conversion {
    val Double.toCelsiusFromKelvin: Double
        get() = (this - 273.15)

    val Double.toFahrenheitFromKelvin: Double
        get() = (this - 273.15) * 9 / 5 + 32

    val Double.roundToUpperInt: Int
        get() = ceil(this).toInt()

    val Double.metersPerSecondToMilesPerHour: Double
        get() = this * 2.23694
    val Double.metersPerSecondToKilometersPerHour: Double
        get() = this * 18 / 5

    fun formatSpeedUnitsWithUnits(
        value: Double,
        speed: Speed,
    ): String {
        return when (speed) {
            Speed.MPS -> "$value m/s"
            Speed.KPH -> "${value.metersPerSecondToKilometersPerHour.roundToUpperInt} KMH"
            Speed.MPH -> "${value.metersPerSecondToMilesPerHour.roundToUpperInt} MPH"
        }
    }

    val Int.toDegrees
        get() = "$this°"

    val String.capitalizeEachFirst
        get() =
            this.split(" ")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

    val currentDate
        get() = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())

    fun getDatOfWeekFromUnixUTC(unixUTC: Long): String {
        return Instant.ofEpochSecond(unixUTC)
            .atZone(ZoneId.of("UTC"))
            .dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.US)
    }

    fun fromKelvinToProvidedUnit(
        value: Double,
        unit: Temperature,
    ) = when (unit) {
        Temperature.CELSIUS -> value.toCelsiusFromKelvin
        Temperature.FAHRENHEIT -> value.toFahrenheitFromKelvin
        Temperature.KELVIN -> value
    }

    fun getTimeFromTimeStamp(
        timeStamp: Long,
        offset: Long,
    ): String {
        val timeStampWithOffset = timeStamp + offset
        val hour =
            Instant.ofEpochSecond(timeStampWithOffset)
                .atZone(ZoneId.of("UTC"))
                .hour

        val minute =
            Instant.ofEpochSecond(timeStampWithOffset)
                .atZone(ZoneId.of("UTC"))
                .minute

        val minuteFormatted =
            when {
                minute < 10 -> "0$minute"
                else -> "$minute"
            }

        val hourFormatted =
            when {
                hour > 12 -> (hour - 12).toString()
                else -> "$hour"
            }

        return "$hourFormatted:$minuteFormatted"
    }

    val Double.fromDoubleToPercentage get() = (this * 100).toInt()

    val hour: DateTimeFormatter get() = DateTimeFormatter.ofPattern("H")

    val Double.decimal get() = BigDecimal(this).setScale(2, RoundingMode.HALF_EVEN)
}
