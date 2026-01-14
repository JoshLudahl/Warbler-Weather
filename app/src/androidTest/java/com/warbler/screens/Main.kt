package com.warbler.screens

import android.view.View
import com.softklass.elk.espresso.view
import com.warbler.R
import org.hamcrest.Matcher

class Main {
    val addLocationIcon: Matcher<View> = view(R.id.search_icon)
    val currentLocationIcon: Matcher<View> = view(R.id.current_location_icon)
    val dateTitle: Matcher<View> = view(R.id.date_title)
    val errorView: Matcher<View> = view(R.id.error_view)
    val loading: Matcher<View> = view(R.id.loading)
    val locationText: Matcher<View> = view(R.id.location_text)
    val settingsIcon: Matcher<View> = view(R.id.settings_icon)
    val airQualityText: Matcher<View> = view("Air Quality Index")
}
