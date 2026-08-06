package com.warbler.feature.weather.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.warbler.feature.weather.R

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class,
)
@Preview
@Composable
fun MenuSample(
    onLocationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    // Icon button should have a tooltip associated with it for a11y.
    TooltipBox(
        positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            PlainTooltip {
                Text("Localized description")
            }
        },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(painterResource(R.drawable.menu_03), contentDescription = "Localized description")
        }
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Edit") },
            onClick = onLocationClick,
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
        )
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = onSettingsClick,
            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
        )
    }
}
