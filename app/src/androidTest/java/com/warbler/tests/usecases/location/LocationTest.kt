package com.warbler.tests.usecases.location

import com.softklass.elk.screen
import com.warbler.screens.Main
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class LocationTest : BaseTest() {
    @Test
    fun checkLocation() {
        screen<Main> {
            // Do nothing
        }
    }
}
