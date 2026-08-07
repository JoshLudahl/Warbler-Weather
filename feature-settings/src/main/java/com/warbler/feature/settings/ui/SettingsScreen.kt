package com.warbler.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.warbler.core.model.units.AccumulationUnit
import com.warbler.core.model.units.SpeedUnit
import com.warbler.core.model.units.TemperatureUnit
import com.warbler.core.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onNavigateUp: () -> Unit = {},
    onReviewAppClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsContent(
        uiState = uiState,
        onTemperatureUnitSelected = viewModel::onTemperatureUnitSelected,
        onSpeedUnitSelected = viewModel::onSpeedUnitSelected,
        onAccumulationUnitSelected = viewModel::onAccumulationUnitSelected,
        onNavigateUp = onNavigateUp,
        onReviewAppClick = onReviewAppClick,
        onAboutClick = onAboutClick,
        onShareClick = onShareClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onTemperatureUnitSelected: (TemperatureUnit) -> Unit = {},
    onSpeedUnitSelected: (SpeedUnit) -> Unit = {},
    onAccumulationUnitSelected: (AccumulationUnit) -> Unit = {},
    onNavigateUp: () -> Unit = {},
    onReviewAppClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
        ) {
            // Units of Measurement
            SectionHeading("Units of Measurement")
            // Temperature
            SubHeading("Temperature")
            Card(
                shape = RoundedCornerShape(30.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SingleSelectButtonGroup(
                        options = TemperatureUnit.entries,
                        selected = uiState.temperatureUnit,
                        label = { it.label },
                        onSelected = onTemperatureUnitSelected,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Speed
            SubHeading("Speed")
            Card(
                shape = RoundedCornerShape(30.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SingleSelectButtonGroup(
                        options = SpeedUnit.entries,
                        selected = uiState.speedUnit,
                        label = { it.label },
                        onSelected = onSpeedUnitSelected,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Accumulation
            SubHeading("Accumulation")
            Card(
                shape = RoundedCornerShape(30.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SingleSelectButtonGroup(
                        options = AccumulationUnit.entries,
                        selected = uiState.accumulationUnit,
                        label = { it.label },
                        onSelected = onAccumulationUnitSelected,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About
            SectionHeading("About")
            Surface(
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Yellow-rumped Warbler",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                    Text(
                        text = "Review App",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Version
            Text(
                text = uiState.appVersion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun SubHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
private fun <T> SingleSelectButtonGroup(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit,
) {
    FlowRow(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEachIndexed { index, option ->
            val checked = option == selected
            ToggleButton(
                checked = checked,
                onCheckedChange = { onSelected(option) },
                colors =
                    ToggleButtonDefaults.toggleButtonColors(
                        checkedContainerColor = MaterialTheme.colorScheme.onSurface,
                        checkedContentColor = MaterialTheme.colorScheme.surface,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                modifier = Modifier.semantics { role = Role.RadioButton },
            ) {
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(
                    text = label(option),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsContent(
            uiState = SettingsUiState(),
        )
    }
}

@PreviewLightDark
@Composable
private fun SectionHeadingPreview() {
    AppTheme {
        SectionHeading("Units of Measurement")
    }
}

@Preview(showBackground = true)
@Composable
private fun SubHeadingPreview() {
    AppTheme {
        SubHeading("Temperature")
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
private fun ButtonGroupPreview() {
    AppTheme {
        SingleSelectButtonGroup(
            options = TemperatureUnit.entries,
            selected = TemperatureUnit.FAHRENHEIT,
            label = { it.label },
            onSelected = {},
        )
    }
}
