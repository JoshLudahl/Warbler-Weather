package com.weatheruous.utilities

import androidx.datastore.preferences.core.intPreferencesKey

object Constants {
    const val ABOUT_URL: String = "https://www.google.com"
    const val PRIVACY_POLICY_URL: String = "https://www.google.com"

    const val TEMPERATURE_UNIT = "temperature_unit"
    val TEMPERATURE_UNIT_PREFERENCES = intPreferencesKey(TEMPERATURE_UNIT)

    const val SPEED_UNIT = "speed_unit"
    val SPEED_UNIT_PREFERENCES = intPreferencesKey(SPEED_UNIT)
}
