package com.warbler.tests

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.softklass.elk.rules.EspressoSetupRule
import com.warbler.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

@HiltAndroidTest
abstract class BaseTest {
    private val hiltAndroidRule = HiltAndroidRule(this)
    private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)
    private val espressoRule = EspressoSetupRule(activityScenarioRule)

    @get:Rule
    val ruleChain: RuleChain =
        RuleChain
            .outerRule(hiltAndroidRule)
            .around(espressoRule)
            .around(activityScenarioRule)

    @Before
    fun init() = hiltAndroidRule.inject()

    fun waitForView(
        matcher: Matcher<View>,
        timeout: Long = 15000,
        interval: Long = 500,
    ) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        while (System.currentTimeMillis() < endTime) {
            try {
                onView(matcher).check(matches(isDisplayed()))
                return // View is displayed, exit function
            } catch (e: Exception) {
                when (e) {
                    is NoMatchingViewException -> {
                        // View not found or not displayed yet, continue waiting
                        Thread.sleep(interval)
                    }
                    else -> throw e
                }
            }
        }

        // Final attempt to throw the actual error if timeout is reached
        onView(matcher).check(matches(isDisplayed()))
    }
}
