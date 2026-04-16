package com.warbler.tests.navigation

import com.softklass.elk.espresso.click
import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.espresso.on
import com.softklass.elk.screen
import com.warbler.robot.LocationRobot
import com.warbler.robot.MainRobot
import com.warbler.robot.SettingsRobot
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class NavigationTest : BaseTest() {
    @Before
    fun waitForLoading() {
        screen<MainRobot> {
            waitForView(loading, ViewStatus.GONE)
        }
    }

    @Test
    fun verifyLocationIconIsDisplayed() {
        screen<MainRobot> {
            addLocationIcon.isDisplayed()
        }
    }

    @Test
    fun verifyLocationTapTakesUserToLocationScreen() {
        screen<MainRobot> { click on addLocationIcon }

        screen<LocationRobot> {
            waitForView(appBarSection)
            appBarSection.isDisplayed()
        }
    }

    @Test
    fun verifySettingsTapTakesUserToSettingsScreen() {
        screen<MainRobot> { click on settingsIcon }

        screen<SettingsRobot> {
            waitForView(settingsTitle)
            settingsTitle.isDisplayed()
        }
    }
}
