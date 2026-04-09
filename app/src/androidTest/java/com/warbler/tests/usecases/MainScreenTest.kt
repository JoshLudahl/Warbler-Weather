package com.warbler.tests.usecases

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.screen
import com.warbler.screens.Main
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class MainScreenTest : BaseTest() {
    @Before
    fun waitForLoading() {
        screen<Main> {
            waitForView(loading, ViewStatus.GONE)
        }
    }

    @Test
    fun verifyCurrentTemperatureShowsFakeValue() {
        screen<Main> {
            currentTemperature.isDisplayed()
            onView(currentTemperature).check(matches(withText("23°")))
        }
    }

    @Test
    fun verifyFeelsLikeShowsFakeValue() {
        screen<Main> {
            feelsLike.isDisplayed()
            onView(feelsLike).check(matches(withText(containsString("22°"))))
        }
    }

    @Test
    fun verifyWeatherDescriptionShowsFakeValue() {
        screen<Main> {
            weatherDescription.isDisplayed()
            onView(weatherDescription).check(matches(withText("Clear Sky")))
        }
    }

    @Test
    fun verifyDateTitleIsDisplayed() {
        screen<Main> {
            dateTitle.isDisplayed()
        }
    }

    @Test
    fun verifyAirQualityLayoutIsDisplayed() {
        screen<Main> {
            airQualityLayout.isDisplayed()
        }
    }

    @Test
    fun verifyAirQualityTextIsDisplayed() {
        screen<Main> {
            airQualityText.isDisplayed()
        }
    }
}
