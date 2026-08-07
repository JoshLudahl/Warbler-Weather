package com.warbler.feature.weather.ui.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.warbler.feature.weather.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun NavigationMenuOptionsExpandedPreview() {
    Box(modifier = Modifier.padding(100.dp)) {
        var expanded by remember { mutableStateOf(true) }
        NavigationMenuOptionsContent(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            usePopup = false, // Use Box for preview
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationMenuOptions(
    onLocationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    NavigationMenuOptionsContent(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        onLocationClick = onLocationClick,
        onSettingsClick = onSettingsClick,
        usePopup = true,
    )
}

@Composable
fun DownwardFabMenuItem(
    onClick: () -> Unit,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = contentColorFor(containerColor),
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        onClick = onClick,
    ) {
        Row(
            Modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            icon()
            text()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NavigationMenuOptionsContent(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLocationClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    usePopup: Boolean = true,
) {
    BackHandler(enabled = expanded) {
        onExpandedChange(false)
    }

    var fabHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val containerColor = MaterialTheme.colorScheme.background

    Box {
        ToggleFloatingActionButton(
            checked = expanded,
            onCheckedChange = onExpandedChange,
            modifier = Modifier.onGloballyPositioned { fabHeight = it.size.height },
            containerColor = { containerColor },
        ) {
            if (expanded) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Menu",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.menu_03),
                    contentDescription = "Open Menu",
                    modifier = Modifier.graphicsLayer { shadowElevation = 0f },
                )
            }
        }

        if (expanded) {
            val content = @Composable {
                val topPadding = if (usePopup) 8.dp else with(density) { fabHeight.toDp() } + 8.dp
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
                ) {
                    Column(
                        modifier = Modifier.padding(top = topPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        DownwardFabMenuItem(
                            onClick = {
                                onExpandedChange(false)
                                onLocationClick()
                            },
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            text = { Text("Location") },
                        )
                        DownwardFabMenuItem(
                            onClick = {
                                onExpandedChange(false)
                                onSettingsClick()
                            },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            text = { Text("Settings") },
                        )
                    }
                }
            }

            if (usePopup) {
                Popup(
                    alignment = Alignment.TopEnd,
                    offset = IntOffset(0, fabHeight),
                    onDismissRequest = { onExpandedChange(false) },
                    properties = PopupProperties(focusable = true),
                    content = content,
                )
            } else {
                content()
            }
        }
    }
}
