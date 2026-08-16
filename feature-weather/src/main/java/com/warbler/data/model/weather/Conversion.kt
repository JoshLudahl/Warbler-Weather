package com.warbler.data.model.weather

import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit
import com.warbler.core.utilities.Constants
import java.text.DecimalFormat
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
        speed: SpeedUnit,
    ): String =
        when (speed) {
            SpeedUnit.MPS -> "$value m/s"
            SpeedUnit.KPH -> "${value.metersPerSecondToKilometersPerHour.roundToUpperInt} KMH"
            SpeedUnit.MPH -> "${value.metersPerSecondToMilesPerHour.roundToUpperInt} MPH"
        }

    fun formatSpeedUnitsWithUnits(
        value: Double,
        speed: SpeedUnit,
    ): Double =
        when (speed) {
            SpeedUnit.MPS -> value
            SpeedUnit.KPH -> value.metersPerSecondToKilometersPerHour
            SpeedUnit.MPH -> value.metersPerSecondToMilesPerHour
        }

    val Int.toDegrees
        get() = "$this°"

    val TemperatureUnit.temperatureSymbol
        get() =
            when (this) {
                TemperatureUnit.CELSIUS -> "c"
                TemperatureUnit.FAHRENHEIT -> "f"
                TemperatureUnit.KELVIN -> "k"
            }

    val String.capitalizeEachFirst
        get() =
            this
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

    val currentDate: String
        get() = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())

    fun getDatOfWeekFromUnixUTC(unixUTC: Long): String =
        Instant
            .ofEpochSecond(unixUTC)
            .atZone(ZoneId.of("UTC"))
            .dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.getDefault())

    fun fromKelvinToProvidedUnit(
        value: Double,
        unit: TemperatureUnit,
    ) = when (unit) {
        TemperatureUnit.CELSIUS -> value.toCelsiusFromKelvin
        TemperatureUnit.FAHRENHEIT -> value.toFahrenheitFromKelvin
        TemperatureUnit.KELVIN -> value
    }

    fun getTimeFromTimeStamp(
        timeStamp: Long,
        offset: Long,
        clockUnit: Int = 0,
    ): String {
        val timeStampWithOffset = timeStamp + offset
        val hour =
            Instant
                .ofEpochSecond(timeStampWithOffset)
                .atZone(ZoneId.of("UTC"))
                .hour

        val minute =
            Instant
                .ofEpochSecond(timeStampWithOffset)
                .atZone(ZoneId.of("UTC"))
                .minute

        val minuteFormatted =
            when {
                minute < 10 -> "0$minute"
                else -> "$minute"
            }

        if (clockUnit == 1) { // H24
            return String.format(Locale.getDefault(), "%02d:%s", hour, minuteFormatted)
        }

        val hourFormatted =
            when {
                hour > 12 -> (hour - 12).toString()
                hour == 0 -> "12"
                else -> "$hour"
            }

        val suffix = if (hour >= 12) " PM" else " AM"

        return "$hourFormatted:$minuteFormatted $suffix"
    }

    val Double.fromDoubleToPercentage get() = (this * 100).toInt()

    val WeatherDataSource.bottomAxisValueFormatter
        get() =
            CartesianValueFormatter { _, value, _ ->

                val addHourlyMilli = (this.hourly[0].dt + value.toInt() * Constants.HOUR).toLong()

                var hour =
                    Instant
                        .ofEpochSecond(addHourlyMilli + this.timezoneOffset)
                        .atZone(ZoneId.of("UTC"))
                        .hour

                var suffix = " AM"
                if (hour >= 12) {
                    hour %= 12
                    suffix = " PM"
                }
                if (hour == 0) hour = 12

                "$hour$suffix"
            }

    val Int.toReadableHour
        get() =
            Instant
                .ofEpochSecond(this.toLong())
                .atZone(ZoneId.of("UTC"))
                .hour

    val Int.fromHourWithSuffix
        get() =
            when {
                this > 12 -> {
                    val hour = this % 12
                    "$hour PM"
                }

                this == 12 -> "$this PM"
                this == 0 -> "12AM"
                else -> "$this AM"
            }
    private val Double.fromMillimetersPerHourToInchesPerHour get(): Double = this / 25.4

    fun convertOrReturnAccumulationByUnit(
        accumulation: Double,
        unit: AccumulationUnit,
    ): Double =
        when (unit) {
            AccumulationUnit.MILLIMETERS_PER_HOUR -> accumulation
            AccumulationUnit.INCHES_PER_HOUR -> accumulation.fromMillimetersPerHourToInchesPerHour
        }

    fun formatAccumulationValue(
        accumulation: Double,
        unit: AccumulationUnit,
    ): String {
        val converted = convertOrReturnAccumulationByUnit(accumulation, unit)
        val unitLabel = if (unit == AccumulationUnit.INCHES_PER_HOUR) "in" else "mm"
        return String.format(Locale.getDefault(), "%.2f %s", converted, unitLabel)
    }

    val wholeNumberValueFormatter =
        CartesianValueFormatter { _, value, _ ->
            value.toInt().toString()
        }

    private val precipDecimalFormat = DecimalFormat("0.###")

    val precipValueFormatter =
        CartesianValueFormatter { _, value, _ ->
            precipDecimalFormat.format(value)
        }
}
