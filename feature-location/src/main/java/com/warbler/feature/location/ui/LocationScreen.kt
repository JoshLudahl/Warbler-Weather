package com.warbler.feature.location.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.theme.AppTypography
import com.warbler.core.utilities.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val locationList by viewModel.locationList.collectAsState()
    val locationSearchList by viewModel.locationSearchList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchBarActive by viewModel.isSearchBarActive.collectAsState()

    Scaffold(
        topBar = {
            if (!isSearchBarActive) {
                TopAppBar(
                    title = {
                        Text(
                            "Locations",
                            fontFamily = AppTypography.titleMedium.fontFamily,
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .semantics { isTraversalGroup = true }
                    .padding(paddingValues),
        ) {
            SearchBar(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .semantics { traversalIndex = 0f }
                        .then(if (isSearchBarActive) Modifier.fillMaxWidth() else Modifier.padding(horizontal = 16.dp)),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { viewModel.onSearchQueryChange(it) },
                        onSearch = { viewModel.onSearchBarActiveChange(false) },
                        expanded = isSearchBarActive,
                        onExpandedChange = { viewModel.onSearchBarActiveChange(it) },
                        placeholder = { Text("Search for a location") },
                        leadingIcon = {
                            if (isSearchBarActive) {
                                IconButton(onClick = { viewModel.onSearchBarActiveChange(false) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                }
                            } else {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        },
                        trailingIcon = {
                            if (isSearchBarActive && searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                    )
                },
                expanded = isSearchBarActive,
                onExpandedChange = { viewModel.onSearchBarActiveChange(it) },
            ) {
                // Expanded Content
                when (val searchState = locationSearchList) {
                    is Resource.Success -> {
                        if (searchState.data.isEmpty() && searchQuery.length >= 3) {
                            Text(
                                "No results found",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(searchState.data) { location ->
                                    ListItem(
                                        headlineContent = { Text(location.name) },
                                        supportingContent = {
                                            Text(
                                                listOfNotNull(location.state, location.country).joinToString(", "),
                                            )
                                        },
                                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                        modifier =
                                            Modifier.clickable {
                                                viewModel.saveToDatabase(location)
                                                viewModel.onSearchBarActiveChange(false)
                                                onNavigateBack()
                                            },
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Searching...")
                        }
                    }
                    is Resource.Error -> {
                        Text(
                            "Error: ${searchState.message}",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            // Background content (Saved Locations)
            if (!isSearchBarActive) {
                Column(
                    modifier =
                        Modifier
                            .padding(top = 80.dp) // Offset for the search bar
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                ) {
                    Text(
                        "Recent Locations",
                        style = AppTypography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )

                    when (val state = locationList) {
                        is Resource.Success -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                items(state.data) { location ->
                                    LocationItem(
                                        location = location,
                                        onClick = {
                                            viewModel.updateCurrentLocation(location)
                                            onNavigateBack()
                                        },
                                    )
                                }
                            }
                        }
                        is Resource.Loading -> Text("Loading...", modifier = Modifier.padding(16.dp))
                        is Resource.Error -> Text("Error: ${state.message}", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LocationItem(
    location: LocationEntity,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                location.name,
                fontWeight = if (location.current) FontWeight.Bold else FontWeight.Normal,
            )
        },
        supportingContent = {
            Text(
                listOfNotNull(location.state, location.country).joinToString(", "),
            )
        },
        leadingContent = {
            Icon(
                if (location.current) Icons.Default.LocationOn else Icons.Default.History,
                contentDescription = null,
                tint = if (location.current) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            )
        },
        trailingContent = {
            if (location.current) {
                Text(
                    "Current",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        modifier =
            Modifier
                .clickable { onClick() }
                .fillMaxWidth(),
    )
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
