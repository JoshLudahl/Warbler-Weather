package com.warbler.feature.location.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.warbler.core.utilities.Resource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: LocationViewModel = hiltViewModel(),
) {
    val locationList by viewModel.locationList.collectAsState()
    val locationSearchList by viewModel.locationSearchList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            delay(500)
            viewModel.searchForLocation(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Search UI
            Row(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search location") },
                    modifier = Modifier.weight(1f),
                )
            }

            // Search Results
            Text("Search Results:", modifier = Modifier.padding(16.dp))
            when (val searchState = locationSearchList) {
                is Resource.Success -> {
                    if (searchState.data.isEmpty()) {
                        Text("No results found", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(searchState.data) { location ->
                                Text(
                                    text = location.toDisplayString,
                                    modifier =
                                        Modifier
                                            .padding(16.dp)
                                            .clickable {
                                                Log.d("LocationScreen", "Location clicked: ${location.name}")
                                                viewModel.saveToDatabase(location)
                                                Log.d("LocationScreen", "Calling onNavigateBack")
                                                onNavigateBack()
                                            },
                                )
                            }
                        }
                    }
                }
                is Resource.Loading -> Text("Searching...", modifier = Modifier.padding(16.dp))
                is Resource.Error -> Text("Error: ${searchState.message}", modifier = Modifier.padding(16.dp))
            }

            // Saved Locations
            Text("Saved Locations:", modifier = Modifier.padding(16.dp))
            when (val state = locationList) {
                is Resource.Success -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.data) { location ->
                            Text(text = location.toDisplayString, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                is Resource.Loading -> Text("Loading...", modifier = Modifier.padding(16.dp))
                is Resource.Error -> Text("Error: ${state.message}", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
