package com.warbler.data.model.weather

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.warbler.ui.settings.Accumulation
import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import com.warbler.utilities.Constants
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

object Conversion {
    private val Double.toCelsiusFromKelvin: Double
        get() = (this - 273.15)

    private val Double.toFahrenheitFromKelvin: Double
        get() = (this - 273.15) * 9 / 5 + 32

    private val Double.roundToUpperInt: Int
        get() = ceil(this).toInt()

    private val Double.metersPerSecondToMilesPerHour: Double
        get() = this * 2.23694
    private val Double.metersPerSecondToKilometersPerHour: Double
        get() = this * 18 / 5

    fun formatSpeedUnitsWithUnitsToString(
        value: Double,
        speed: Speed,
    ): String {
        return when (speed) {
            Speed.MPS -> "$value m/s"
            Speed.KPH -> "${value.metersPerSecondToKilometersPerHour.roundToUpperInt} KMH"
            Speed.MPH -> "${value.metersPerSecondToMilesPerHour.roundToUpperInt} MPH"
        }
    }

    fun formatSpeedUnitsWithUnits(
        value: Double,
        speed: Speed,
    ): Double {
        return when (speed) {
            Speed.MPS -> value
            Speed.KPH -> value.metersPerSecondToKilometersPerHour
            Speed.MPH -> value.metersPerSecondToMilesPerHour
        }
    }

    val Int.toDegrees
        get() = "$this°"

    val Temperature.temperatureSymbol
        get() =
            when (this) {
                Temperature.CELSIUS -> "c"
                Temperature.FAHRENHEIT -> "f"
                Temperature.KELVIN -> "k"
            }

    val String.capitalizeEachFirst
        get() =
            this.split(" ")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

    val currentDate: String
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

    val WeatherDataSource.bottomAxisValueFormatter
        get() =
            AxisValueFormatter<AxisPosition.Horizontal.Bottom> { x, _, _ ->

                val addHourlyMilli = (this.hourly[0].dt + x.toInt() * Constants.HOUR).toLong()

                var hour =
                    Instant.ofEpochSecond(addHourlyMilli + this.timezoneOffset)
                        .atZone(ZoneId.of("UTC"))
                        .hour

                var suffix = "AM"
                if (hour >= 12) {
                    hour %= 12
                    suffix = "PM"
                }
                if (hour == 0) hour = 12

                "$hour$suffix"
            }

    val Int.toReadableHour
        get() =
            Instant.ofEpochSecond(this.toLong())
                .atZone(ZoneId.of("UTC"))
                .hour

    val Int.fromHourWithSuffix
        get() =
            when {
                this > 12 -> {
                    val hour = this % 12
                    "${hour}PM"
                }

                this == 12 -> "${this}PM"
                this == 0 -> "12AM"
                else -> "${this}AM"
            }
    private val Double.fromMillimetersPerHourToInchesPerHour get(): Double = this / 25.4

    fun convertOrReturnAccumulationByUnit(
        accumulation: Double,
        unit: Accumulation,
    ): Double {
        return when (unit) {
            Accumulation.MILLIMETERS_PER_HOUR -> accumulation
            Accumulation.INCHES_PER_HOUR -> accumulation.fromMillimetersPerHourToInchesPerHour
        }
    }
}
