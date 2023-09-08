package com.warbler.tests.suite

import com.warbler.tests.usecases.location.LocationTest
import com.warbler.tests.usecases.main.MainScreenTest
import com.warbler.tests.usecases.settings.SettingsTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(
    LocationTest::class
)
class LocationScreenTestSuite

@RunWith(Suite::class)
@SuiteClasses(
    MainScreenTest::class
)
class MainScreenTestSuite

@RunWith(Suite::class)
@SuiteClasses(
    SettingsTest::class
)
class SettingsScreenTestSuite

@RunWith(Suite::class)
@SuiteClasses(
    LocationScreenTestSuite::class,
    MainScreenTestSuite::class,
    SettingsScreenTestSuite::class
)
class TestSuite
