package com.weatheruous.data.model.weather

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
import kotlin.math.roundToInt

object Conversion {

    val Double.toCelsiusFromKelvin: Double
        get() = (this - 273.15)

    val Double.toFahrenheitFromKelvin: Double
        get() = (this - 273.15) * 9 / 5 + 32

    val Double.roundToTwoDecimalPlaces
        get() = (this * 100.0).roundToInt() / 100.0

    val Int.toDegrees
        get() = "$this°"

    val String.capitalizeEachFirst
        get() = this.split(" ")
            .joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

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
