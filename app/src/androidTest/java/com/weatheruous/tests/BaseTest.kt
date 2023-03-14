package com.weatheruous.tests

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.weatheruous.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

@HiltAndroidTest
abstract class BaseTest {

    private val hiltAndroidRule = HiltAndroidRule(this)
    private val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val ruleChain: RuleChain = RuleChain
        .outerRule(hiltAndroidRule)
        .around(activityScenarioRule)

    @Before
    fun init() = hiltAndroidRule.inject()
}
