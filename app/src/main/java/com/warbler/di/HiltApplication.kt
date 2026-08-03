package com.warbler.di

import android.app.Application
import com.warbler.R
import com.warbler.core.network.NetworkConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkConstants.init(
            getString(R.string.WEATHER_BASE_URL),
            getString(R.string.WEATHER_API_KEY),
        )
    }
}
