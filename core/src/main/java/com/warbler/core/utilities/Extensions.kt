package com.warbler.core.utilities

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.serialization.json.Json

fun Context.showToast(message: String) {
    Toast
        .makeText(
            this,
            message,
            Toast.LENGTH_SHORT,
        ).show()
}

fun Context.openInCustomTab(url: String) {
    try {
        val uri = url.toUri()
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, uri)
    } catch (e: Exception) {
        showToast("Error opening link")
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.launchReviewFlow() {
    val manager = ReviewManagerFactory.create(this)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo = task.result
            val activity = findActivity()
            if (activity != null) {
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
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
