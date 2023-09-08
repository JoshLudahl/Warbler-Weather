package com.warbler.tests.usecases.main

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
class MainScreenTest : BaseTest() {

    @Before
    fun waitForLoaded() {
        // TODO Handle loading indicator
        Thread.sleep(1500)
    }

    @Test
    fun verifyMainScreenAddLocationIcon() {
        screen<Main> {
            addLocationIcon.isDisplayed()
        }
    }

    @Test
    fun verifyNavigationToSettings() {
        screen<Main> { click on settingsIcon }
        Thread.sleep(1500)
        screen<Settings> { settingsTitle.isDisplayed() }
    }

    @Test
    fun verifyNavigationToSearchLocation() {
        screen<Main> { click on addLocationIcon }
        Thread.sleep(1500)
        screen<Location> { locationTitle.isDisplayed() }
    }

    @Test
    fun verifyMainScreenCurrentLocationIcon() {
        screen<Main> {
            currentLocationIcon.isDisplayed()
        }
    }

    @Test
    fun verifyMainScreenLocationText() {
        screen<Main> {
            locationText.isDisplayed()
        }
    }

    @Test
    fun verifyMainScreenSettingsIcon() {
        screen<Main> {
            settingsIcon.isDisplayed()
        }
    }
}
