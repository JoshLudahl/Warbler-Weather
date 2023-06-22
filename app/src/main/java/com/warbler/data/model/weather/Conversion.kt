package com.warbler.data.model.weather

import com.warbler.ui.settings.Speed
import com.warbler.ui.settings.Temperature
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

object Conversion {

    val Double.toCelsiusFromKelvin: Double
        get() = (this - 273.15)

    val Double.toFahrenheitFromKelvin: Double
        get() = (this - 273.15) * 9 / 5 + 32

    val Double.roundToTwoDecimalPlaces
        get() = (this * 100.0).roundToInt() / 100.0

    val Double.roundToUpperInt: Int
        get() = ceil(this).toInt()
    val Double.metersPerSecondToMilesPerHour: Double
        get() = this * 2.23694

    fun formatSpeedUnitsWithUnits(value: Double, speed: Speed): String {
        return when (speed) {
            Speed.KPH -> "$value m/s"
            Speed.MPH -> "${value.metersPerSecondToMilesPerHour.roundToUpperInt} MPH"
        }
    }

    val Int.toDegrees
        get() = "$this°"

    val String.capitalizeEachFirst
        get() = this.split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

    val currentDate
        get() = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())

    /**
     * Formats a date of 2023-03-27T19:32:43.978Z to 'Day-of-week, Month day-of-month-suffix'
     *
     * Example: Monday, March 27th
     */
    fun getFormattedDateFromTimeStamp(instant: Instant): String {
        val instantTrimmed = "${instant.toString().substring(0, 23)}Z"
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date = LocalDate.parse(instantTrimmed, format)
        val dayOfWeek = date.dayOfWeek.toString().lowercase().capitalizeEachFirst
        val month = date.month.toString().lowercase().capitalizeEachFirst
        val dayOfMonth = date.dayOfMonth
        return "$dayOfWeek, $month ${dayOfMonth.appendSuffix}"
    }

    fun getDatOfWeekFromUnixUTC(unixUTC: Long): String {
        return Instant.ofEpochSecond(unixUTC)
            .atZone(ZoneId.of("UTC"))
            .dayOfWeek
            .getDisplayName(TextStyle.FULL, Locale.US)
    }

    fun fromKelvinToProvidedUnit(value: Double, unit: Temperature) =
        when (unit) {
            Temperature.CELSIUS -> value.toCelsiusFromKelvin
            Temperature.FAHRENHEIT -> value.toFahrenheitFromKelvin
            Temperature.KELVIN -> value
        }

    fun getTimeFromTimeStamp(timeStamp: Long, offset: Long): String {
        val timeStampWithOffset = timeStamp + offset
        val hour = Instant.ofEpochSecond(timeStampWithOffset)
            .atZone(ZoneId.of("UTC"))
            .hour

        val minute = Instant.ofEpochSecond(timeStampWithOffset)
            .atZone(ZoneId.of("UTC"))
            .minute

        val minuteFormatted = when {
            minute < 10 -> "0$minute"
            else -> "$minute"
        }

        val hourFormatted = when {
            hour > 12 -> (hour - 12).toString()
            else -> "$hour"
        }

        return "$hourFormatted:$minuteFormatted"
    }

    val Double.fromDoubleToPercentage get() = (this * 100).toInt()

    private val Int.appendSuffix
        get() = "$this${getSuffix(this)}"

    private fun getSuffix(day: Int): String {
        if (day in 4..20) return "th"
        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }
}
