package com.warbler.feature.weather.ui.current

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTheme

@Composable
fun WeatherScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                )
                Text(
                    text = "City Name",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = "Weather Icon",
                )
                Text(
                    text = "25°C",
                    style = MaterialTheme.typography.displayLarge,
                )
                Text(
                    text = "Feels like 23°C",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                )
                Text(
                    text = "Brief description",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherScreenPreview() {
    AppTheme {
        WeatherScreen()
    }
}
