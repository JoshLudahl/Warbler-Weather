package com.warbler.screens

import android.view.View
import com.softklass.elk.espresso.view
import com.warbler.R
import org.hamcrest.Matcher

class Main {
    val currentLocationIcon: Matcher<View> = view(R.id.location_icon)
    val locationText: Matcher<View> = view(R.id.location_text)
    val addLocationIcon: Matcher<View> = view(R.id.search_icon)
    val settingsIcon: Matcher<View> = view(R.id.settings_icon)
}
