package com.warbler.utilities

import android.content.Context
import android.widget.Toast
import kotlinx.serialization.json.Json

fun Context.showToast(message: String) {
    Toast
        .makeText(
            this,
            message,
            Toast.LENGTH_SHORT,
        ).show()
}

inline fun <reified R : Any> String.convertToDataClass(): R {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString(this)
}

fun areNonZeroValuesFound(values: List<Double>): Boolean {
    values.forEach { value ->
        if (value != 0.0) return true
    }
    return false
}

fun doesAnyListContainValues(list: List<List<Double>>): Boolean {
    list.forEach { item ->
        if (areNonZeroValuesFound(item)) return true
    }
    return false
}
