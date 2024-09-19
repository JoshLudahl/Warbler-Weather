package com.warbler.data.model.weather

enum class AqiCode {
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
): AqiCode =
    when (code) {
        "co" -> getC0Index(value)
        "nh3" -> getNh3Index()
        "no" -> getNoIndex()
        "no2" -> getNo2Index(value)
        "03" -> getO3Index(value)
        "pm10" -> getPm10Index(value)
        "pm25" -> getPm25Index(value)
        "so2" -> getSo2Index(value)
        else -> AqiCode.INVALID
    }

fun getSo2Index(value: Double): AqiCode =
    when {
        value < 20.00 -> AqiCode.GOOD
        value in 20.01..80.00 -> AqiCode.FAIR
        value in 80.01..250.00 -> AqiCode.MODERATE
        value in 250.01..350.00 -> AqiCode.POOR
        value > 350.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getNo2Index(value: Double): AqiCode =
    when {
        value < 40.00 -> AqiCode.GOOD
        value in 40.01..70.00 -> AqiCode.FAIR
        value in 70.01..150.00 -> AqiCode.MODERATE
        value in 150.01..200.00 -> AqiCode.POOR
        value > 200.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getPm10Index(value: Double): AqiCode =
    when {
        value < 20.00 -> AqiCode.GOOD
        value in 20.01..50.00 -> AqiCode.FAIR
        value in 50.01..100.00 -> AqiCode.MODERATE
        value in 100.01..200.00 -> AqiCode.POOR
        value > 200.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getPm25Index(value: Double): AqiCode =
    when {
        value < 10.00 -> AqiCode.GOOD
        value in 10.01..25.00 -> AqiCode.FAIR
        value in 25.01..50.00 -> AqiCode.MODERATE
        value in 50.01..75.00 -> AqiCode.POOR
        value > 75.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getO3Index(value: Double): AqiCode =
    when {
        value < 60.00 -> AqiCode.GOOD
        value in 60.01..100.00 -> AqiCode.FAIR
        value in 100.01..140.00 -> AqiCode.MODERATE
        value in 140.01..180.00 -> AqiCode.POOR
        value > 180.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getC0Index(value: Double): AqiCode =
    when {
        value < 4400.00 -> AqiCode.GOOD
        value in 4400.01..9400.00 -> AqiCode.FAIR
        value in 9400.01..12400.00 -> AqiCode.MODERATE
        value in 12400.01..15400.00 -> AqiCode.POOR
        value > 15400.00 -> AqiCode.VERY_POOR
        else -> AqiCode.INVALID
    }

fun getNh3Index(): AqiCode = AqiCode.INVALID

fun getNoIndex(): AqiCode = AqiCode.INVALID
