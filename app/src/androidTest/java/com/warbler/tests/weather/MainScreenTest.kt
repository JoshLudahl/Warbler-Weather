package com.warbler.tests.weather

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.softklass.elk.espresso.isDisplayed
import com.softklass.elk.screen
import com.warbler.robot.MainRobot
import com.warbler.tests.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class MainScreenTest : BaseTest() {
    @Before
    fun waitForLoading() {
        screen<MainRobot> {
            waitForView(loading, ViewStatus.GONE)
        }
    }

    @Test
    fun verifyCurrentTemperatureShowsFakeValue() {
        screen<MainRobot> {
            currentTemperature.isDisplayed()
            onView(currentTemperature).check(matches(withText("23°")))
        }
    }

    @Test
    fun verifyFeelsLikeShowsFakeValue() {
        screen<MainRobot> {
            feelsLike.isDisplayed()
            onView(feelsLike).check(matches(withText(containsString("22°"))))
        }
    }

    @Test
    fun verifyWeatherDescriptionShowsFakeValue() {
        screen<MainRobot> {
            weatherDescription.isDisplayed()
            onView(weatherDescription).check(matches(withText("Clear Sky")))
        }
    }

    @Test
    fun verifyDateTitleIsDisplayed() {
        screen<MainRobot> {
            dateTitle.isDisplayed()
        }
    }

    @Test
    fun verifyAirQualityLayoutIsDisplayed() {
        screen<MainRobot> {
            airQualityLayout.isDisplayed()
        }
    }

    @Test
    fun verifyAirQualityTextIsDisplayed() {
        screen<MainRobot> {
            airQualityText.isDisplayed()
        }
    }
}
