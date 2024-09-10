package com.warbler.data.model.weather

enum class AQI {
    GOOD,
    FAIR,
    MODERATE,
    POOR,
    VERY_POOR,
    INVALID,
}

fun getIndexByCode(
    code: String,
    value: Double,
): AQI =
    when (code) {
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

fun getSo2Index(value: Double): AQI =
    when {
        value < 20.00 -> AQI.GOOD
        value in 20.01..80.00 -> AQI.FAIR
        value in 80.01..250.00 -> AQI.MODERATE
        value in 250.01..350.00 -> AQI.POOR
        value > 350.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getNo2Index(value: Double): AQI =
    when {
        value < 40.00 -> AQI.GOOD
        value in 40.01..70.00 -> AQI.FAIR
        value in 70.01..150.00 -> AQI.MODERATE
        value in 150.01..200.00 -> AQI.POOR
        value > 200.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getPm10Index(value: Double): AQI =
    when {
        value < 20.00 -> AQI.GOOD
        value in 20.01..50.00 -> AQI.FAIR
        value in 50.01..100.00 -> AQI.MODERATE
        value in 100.01..200.00 -> AQI.POOR
        value > 200.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getPm25Index(value: Double): AQI =
    when {
        value < 10.00 -> AQI.GOOD
        value in 10.01..25.00 -> AQI.FAIR
        value in 25.01..50.00 -> AQI.MODERATE
        value in 50.01..75.00 -> AQI.POOR
        value > 75.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getO3Index(value: Double): AQI =
    when {
        value < 60.00 -> AQI.GOOD
        value in 60.01..100.00 -> AQI.FAIR
        value in 100.01..140.00 -> AQI.MODERATE
        value in 140.01..180.00 -> AQI.POOR
        value > 180.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getC0Index(value: Double): AQI =
    when {
        value < 4400.00 -> AQI.GOOD
        value in 4400.01..9400.00 -> AQI.FAIR
        value in 9400.01..12400.00 -> AQI.MODERATE
        value in 12400.01..15400.00 -> AQI.POOR
        value > 15400.00 -> AQI.VERY_POOR
        else -> AQI.INVALID
    }

fun getNh3Index(): AQI = AQI.INVALID

fun getNoIndex(): AQI = AQI.INVALID
