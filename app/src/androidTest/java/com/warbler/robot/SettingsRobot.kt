package com.warbler.robot

import android.view.View
import com.softklass.elk.espresso.view
import com.warbler.R
import org.hamcrest.Matcher

class SettingsRobot {
    val settingsTitle: Matcher<View> = view(R.id.topAppBar)
}
