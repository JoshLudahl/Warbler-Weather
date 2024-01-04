package com.warbler.utilities

import com.patrykandpatrick.vico.core.axis.AxisItemPlacer

object Constants {
    const val ABOUT_URL: String = "https://softklass.com/weatheruous/"
    const val PRIVACY_POLICY_URL: String = "https://softklass.com/privacy-policy/weatheruous.html"
    const val WARBLER_AUDUBON: String =
        "https://www.audubon.org/field-guide/bird/yellow-rumped-warbler"

    const val FLEXIBLE_UPDATE_COUNT: Int = 30
    const val UPDATE_TAG: Int = 1
    const val HOUR = 3600
    val CHART_COLUMN_DEFAULT = AxisItemPlacer.Vertical.default(maxItemCount = { 4 })
    val CHART_LINE_DEFAULT = AxisItemPlacer.Vertical.default(maxItemCount = { 6 })
    const val TEMP_RANGE = 5
}
