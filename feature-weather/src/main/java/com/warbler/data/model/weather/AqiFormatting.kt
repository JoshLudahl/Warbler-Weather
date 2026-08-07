package com.warbler.data.model.weather

import androidx.compose.ui.graphics.Color
import com.warbler.core.theme.aqi1Good
import com.warbler.core.theme.aqi2Fair
import com.warbler.core.theme.aqi3Moderate
import com.warbler.core.theme.aqi4Poor
import com.warbler.core.theme.aqi5VeryPoor

fun aqiColorForLevel(level: Int): Color =
    when (level) {
        1 -> aqi1Good
        2 -> aqi2Fair
        3 -> aqi3Moderate
        4 -> aqi4Poor
        5 -> aqi5VeryPoor
        else -> Color.Gray
    }

data class AqiLevelInfo(
    val level: Int,
    val color: Color,
    val description: String,
)

val aqiLevelInfoList =
    listOf(
        AqiLevelInfo(1, aqi1Good, "1 — Good: Air quality is considered satisfactory; little or no risk."),
        AqiLevelInfo(2, aqi2Fair, "2 — Fair: Acceptable air quality; some pollutants may mildly affect sensitive individuals."),
        AqiLevelInfo(3, aqi3Moderate, "3 — Moderate: Sensitive groups may experience irritation or breathing discomfort."),
        AqiLevelInfo(4, aqi4Poor, "4 — Poor: Unhealthy for sensitive groups; increased likelihood of adverse effects."),
        AqiLevelInfo(5, aqi5VeryPoor, "5 — Very Poor: Unhealthy for everyone; significant risk of adverse effects."),
    )
