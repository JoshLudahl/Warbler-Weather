package com.warbler.di

import android.app.Application
import com.warbler.data.network.NetworkConstants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkConstants.init(this)
    }
}
