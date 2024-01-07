package com.warbler.screens

import android.view.View
import com.softklass.elk.espresso.view
import com.warbler.R
import org.hamcrest.Matcher

class Main {
    val addLocationIcon: Matcher<View> = view(R.id.search_icon)
    val currentLocationIcon: Matcher<View> = view(R.id.current_location_icon)
    val errorView: Matcher<View> = view(R.id.error_view)
    val locationText: Matcher<View> = view(R.id.location_text)
    val settingsIcon: Matcher<View> = view(R.id.settings_icon)
}
