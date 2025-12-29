package com.warbler.tests.usecases

import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.screen
import com.warbler.screens.Main
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class DemoTest : BaseTest() {
    @Before
    fun waitForLoading() {
        screen<Main> {
            waitForView(addLocationIcon)
        }
    }

    @Test
    fun verifyLocationIconIsDisplayed() {
        screen<Main> {
            addLocationIcon.isDisplayed()
        }
    }
}
