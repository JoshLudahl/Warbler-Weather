package com.warbler.feature.weather.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.warbler.core.theme.AppTypography
import com.warbler.feature.weather.ui.main.WeatherUiState

@Composable
fun MainWeatherCard(
    weatherUiState: WeatherUiState,
    icon: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = weatherUiState.description,
                    style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Bold),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = weatherUiState.temperature,
                    style = AppTypography.displayMedium,
                )

                // Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = weatherUiState.feelsLike,
                    style = AppTypography.bodySmall,
                )
            }

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
            )
        }
    }
}
