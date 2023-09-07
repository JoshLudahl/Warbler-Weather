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
import org.junit.Test

@HiltAndroidTest
class MainScreenTest : BaseTest() {

    @Test
    fun verifyMainScreen() {
        screen<Main> {
            addLocationIcon.isDisplayed()
            currentLocationIcon.isDisplayed()
            locationText.isDisplayed()
            settingsIcon.isDisplayed()
        }
    }

    @Test
    fun verifyNavigationToSettings() {
        screen<Main> { click on settingsIcon }
        screen<Settings> { settingsTitle.isDisplayed() }
    }

    @Test
    fun verifyNavigationToSearchLocation() {
        screen<Main> { click on addLocationIcon }
        screen<Location> { locationTitle.isDisplayed() }
    }
}
