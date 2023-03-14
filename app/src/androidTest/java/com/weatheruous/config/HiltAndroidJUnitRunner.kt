package com.weatheruous.config

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom implementation of the Android JUnit Runner for running Hilt UI tests.
 * The class has the suppressed unused annotation because it's a runner and not recognized
 * as being used.
 */
@Suppress("unused")
class HiltAndroidJUnitRunner : AndroidJUnitRunner() {
    override fun newApplication(
            cl: ClassLoader?,
            name: String?,
            context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
