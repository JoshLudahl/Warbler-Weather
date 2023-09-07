package com.warbler.screens

import android.view.View
import com.softklass.elk.espresso.view
import com.warbler.R
import org.hamcrest.Matcher

class Location {
    val locationTitle: Matcher<View> = view(R.id.location_title_text)
}
