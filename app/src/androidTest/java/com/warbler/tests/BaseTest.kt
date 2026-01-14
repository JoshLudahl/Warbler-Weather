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
import org.hamcrest.CoreMatchers.not
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

    enum class ViewStatus {
        DISPLAYED,
        GONE,
    }

    fun waitForView(
        matcher: Matcher<View>,
        status: ViewStatus = ViewStatus.DISPLAYED,
        timeout: Long = 15000,
        interval: Long = 500,
    ) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout

        while (System.currentTimeMillis() < endTime) {
            try {
                when (status) {
                    ViewStatus.DISPLAYED -> onView(matcher).check(matches(isDisplayed()))
                    ViewStatus.GONE -> {
                        try {
                            onView(matcher).check(matches(not(isDisplayed())))
                        } catch (e: NoMatchingViewException) {
                            // View does not exist, which is also fine for GONE
                        }
                    }
                }
                return // Condition met, exit function
            } catch (e: Exception) {
                when (e) {
                    is NoMatchingViewException -> {
                        // View condition not met, continue waiting
                        Thread.sleep(interval)
                    }
                    else -> throw e
                }
            }
        }

        // Final attempt to throw the actual error if timeout is reached
        when (status) {
            ViewStatus.DISPLAYED -> onView(matcher).check(matches(isDisplayed()))
            ViewStatus.GONE -> {
                try {
                    onView(matcher).check(matches(not(isDisplayed())))
                } catch (e: NoMatchingViewException) {
                    // View does not exist, which is also fine for GONE
                }
            }
        }
    }
}
