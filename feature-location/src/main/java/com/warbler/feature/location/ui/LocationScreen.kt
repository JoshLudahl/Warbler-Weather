package com.warbler.feature.location.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warbler.core.model.location.LocationEntity
import com.warbler.core.theme.AppTypography
import com.warbler.core.utilities.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit = {},
    onLocationSelected: () -> Unit = onNavigateBack,
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val locationList by viewModel.locationList.collectAsState()
    val locationSearchList by viewModel.locationSearchList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchBarActive by viewModel.isSearchBarActive.collectAsState()
    val currentLocationSaved by viewModel.currentLocationSaved.collectAsState()
    val isLoadingCurrentLocation by viewModel.isLoadingCurrentLocation.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val textFieldState = rememberTextFieldState(searchQuery)
    val searchBarState =
        rememberSearchBarState(
            initialValue = if (isSearchBarActive) SearchBarValue.Expanded else SearchBarValue.Collapsed,
        )
    val coroutineScope = rememberCoroutineScope()

    val isExpanded = searchBarState.currentValue == SearchBarValue.Expanded

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(searchBarState.currentValue) {
        viewModel.onSearchBarActiveChange(isExpanded)
    }

    LaunchedEffect(isSearchBarActive) {
        if (isSearchBarActive && !isExpanded) {
            searchBarState.animateToExpanded()
        } else if (!isSearchBarActive && isExpanded) {
            searchBarState.animateToCollapsed()
        }
    }

    LaunchedEffect(textFieldState.text) {
        viewModel.onSearchQueryChange(textFieldState.text.toString())
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery != textFieldState.text.toString()) {
            textFieldState.setTextAndPlaceCursorAtEnd(searchQuery)
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            val isGranted =
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (isGranted) {
                viewModel.getCurrentLocationAndSave()
            }
        }

    LaunchedEffect(currentLocationSaved) {
        if (currentLocationSaved) {
            onLocationSelected()
            viewModel.resetCurrentLocationSaved()
        }
    }

    Scaffold(
        topBar = {
            if (!isExpanded) {
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
                    actions = {
                        IconButton(onClick = {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }) {
                            Icon(Icons.Rounded.MyLocation, contentDescription = "My Location")
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .semantics { isTraversalGroup = true }
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = {
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        },
                        placeholder = { Text("Search for a location") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                    )
                },
                modifier =
                    Modifier
                        .semantics { traversalIndex = 0f }
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
            )

            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = {
                    SearchBarDefaults.InputField(
                        textFieldState = textFieldState,
                        searchBarState = searchBarState,
                        onSearch = {
                            coroutineScope.launch { searchBarState.animateToCollapsed() }
                        },
                        placeholder = { Text("Search for a location") },
                        leadingIcon = {
                            IconButton(onClick = {
                                coroutineScope.launch { searchBarState.animateToCollapsed() }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        },
                        trailingIcon = {
                            if (textFieldState.text.isNotEmpty()) {
                                IconButton(onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                    )
                },
            ) {
                // Expanded Content
                when (val searchState = locationSearchList) {
                    is Resource.Success -> {
                        if (searchState.data.isEmpty() && textFieldState.text.length >= 3) {
                            Text(
                                "No results found",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                items(searchState.data) { location ->
                                    ListItem(
                                        supportingContent = {
                                            Text(
                                                listOfNotNull(location.state, location.country).joinToString(", "),
                                            )
                                        },
                                        leadingContent = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                                        modifier =
                                            Modifier.clickable {
                                                viewModel.saveToDatabase(location)
                                                coroutineScope.launch { searchBarState.animateToCollapsed() }
                                                onLocationSelected()
                                            },
                                    ) {
                                        Text(location.name)
                                    }
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
            if (!isExpanded) {
                Column(
                    modifier =
                        Modifier
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
                                            onLocationSelected()
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

        if (isLoadingCurrentLocation) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) {},
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                )
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
    ) {
        Text(
            location.name,
            fontWeight = if (location.current) FontWeight.Bold else FontWeight.Normal,
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}
