package com.warbler.utilities

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object DataPref {

    const val TEMPERATURE_UNIT = "temperature_unit"
    val TEMPERATURE_UNIT_PREFERENCES = intPreferencesKey(TEMPERATURE_UNIT)

    const val SPEED_UNIT = "speed_unit"
    val SPEED_UNIT_PREFERENCES = intPreferencesKey(SPEED_UNIT)

    fun readIntDataStoreFlow(key: String, dataStore: DataStore<Preferences>): Flow<Int> =
        dataStore.data
            .catch {
                emit(emptyPreferences())
            }.map { preferences ->
                preferences[intPreferencesKey(key)] ?: 0
            }

    suspend fun saveIntDataStore(key: String, value: Int, dataStore: DataStore<Preferences>) {
        val dataStoreKey = intPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[dataStoreKey] = value
        }
    }
}
