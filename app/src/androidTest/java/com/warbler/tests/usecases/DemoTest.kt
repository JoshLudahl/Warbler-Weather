package com.warbler.tests.usecases

import com.softklass.elk.espresso.click
import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.espresso.on
import com.softklass.elk.screen
import com.warbler.screens.Location
import com.warbler.screens.Main
import com.warbler.screens.Settings
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class DemoTest : BaseTest() {
    @Before
    fun waitForLoading() {
        screen<Main> {
            waitForView(airQualityText)
        }
    }

    @Test
    fun verifyLocationIconIsDisplayed() {
        screen<Main> {
            addLocationIcon.isDisplayed()
        }
    }

    @Test
    fun verifyLocationTapTakesUserToLocationScreen() {
        screen<Main> { click on addLocationIcon }

        screen<Location> {
            waitForView(appBarSection)
            appBarSection.isDisplayed()
        }
    }

    @Test
    fun verifySettingsTapTakesUserToSettingsScreen() {
        screen<Main> { click on settingsIcon }

        screen<Settings> {
            waitForView(settingsTitle)
            settingsTitle.isDisplayed()
        }
    }
}
