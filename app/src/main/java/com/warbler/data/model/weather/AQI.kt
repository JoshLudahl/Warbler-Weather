package com.warbler.data.model.weather

enum class AQI {
    GOOD,
    FAIR,
    MODERATE,
    POOR,
    VERY_POOR,
    INVALID,
}

fun getIndexByCode(code: String, value: Int): AQI = when (code) {
    "co" -> getC0Index(value)
    "nh3" -> getNh3Index()
    "no" -> getNoIndex()
    "no2" -> getNo2Index(value)
    "03" -> getO3Index(value)
    "pm10" -> getPm10Index(value)
    "pm25" -> getPm25Index(value)
    "so2" -> getSo2Index(value)
    else -> AQI.INVALID
}

fun getSo2Index(value: Int): AQI =
    when {
        value < 20 -> AQI.GOOD
        value in 21..80 -> AQI.FAIR
        value in 81..250 -> AQI.MODERATE
        value in 251..350 -> AQI.POOR
        value > 350 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getNo2Index(value: Int): AQI =
    when {
        value < 40 -> AQI.GOOD
        value in 41..70 -> AQI.FAIR
        value in 71..150 -> AQI.MODERATE
        value in 151..200 -> AQI.POOR
        value > 200 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getPm10Index(value: Int): AQI =
    when {
        value < 20 -> AQI.GOOD
        value in 21..50 -> AQI.FAIR
        value in 51..100 -> AQI.MODERATE
        value in 101..200 -> AQI.POOR
        value > 201 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getPm25Index(value: Int): AQI =
    when {
        value < 10 -> AQI.GOOD
        value in 11..25 -> AQI.FAIR
        value in 26..50 -> AQI.MODERATE
        value in 51..75 -> AQI.POOR
        value > 76 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getO3Index(value: Int): AQI =
    when {
        value < 60 -> AQI.GOOD
        value in 61..100 -> AQI.FAIR
        value in 101..140 -> AQI.MODERATE
        value in 141..180 -> AQI.POOR
        value > 181 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getC0Index(value: Int): AQI =
    when {
        value < 4400 -> AQI.GOOD
        value in 4401..9400 -> AQI.FAIR
        value in 9401..12400 -> AQI.MODERATE
        value in 12401..15400 -> AQI.POOR
        value > 15401 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getNh3Index(): AQI = AQI.INVALID

fun getNoIndex(): AQI = AQI.INVALID
