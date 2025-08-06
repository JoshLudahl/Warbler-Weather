package com.warbler.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.warbler.MainActivity
import com.warbler.R
import com.warbler.data.model.weather.WeatherIconSelection.getIconForCondition
import com.warbler.data.repositories.location.LocationRepository
import com.warbler.data.repositories.weather.WeatherDatabaseRepository
import com.warbler.data.repositories.weather.WeatherNetworkRepository
import com.warbler.utilities.DataPref
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Weather Widget Provider that displays current weather and location information
 * on the home screen.
 */
@AndroidEntryPoint
class WeatherWidgetProvider : AppWidgetProvider() {
    @Inject
    lateinit var weatherNetworkRepository: WeatherNetworkRepository

    @Inject
    lateinit var weatherDatabaseRepository: WeatherDatabaseRepository

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "WeatherWidgetProvider"
        const val ACTION_UPDATE_WIDGET = "com.warbler.widget.UPDATE_WIDGET"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        Log.d(TAG, "onUpdate called for ${appWidgetIds.size} widgets")

        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.d(TAG, "Widget enabled")
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        Log.d(TAG, "Widget disabled")
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_UPDATE_WIDGET -> {
                Log.d(TAG, "Manual update requested")
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds =
                    appWidgetManager.getAppWidgetIds(
                        android.content.ComponentName(context, WeatherWidgetProvider::class.java),
                    )
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
    ) {
        Log.d(TAG, "Updating widget $appWidgetId")

        // Create RemoteViews object for the widget layout
        val views = RemoteViews(context.packageName, R.layout.weather_widget)

        // Set up click intent to open the main app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        // Set up refresh button click intent
        val refreshIntent =
            Intent(context, WeatherWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
        val refreshPendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

        // Load and display weather data
        loadWeatherData(context, appWidgetManager, appWidgetId, views)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun loadWeatherData(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
    ) {
        widgetScope.launch {
            try {
                // Get current location from database
                val location = locationRepository.getCurrentLocationFromDatabase().first()
                val locationState = if (location.state == null) "" else ", ${location.state}"
                val locationName = "${location.name}$locationState"

                // Try to get cached weather data first
                val cachedWeather = weatherDatabaseRepository.getCurrentWeatherSync()
                Log.d(TAG, "Cached weather data: $cachedWeather")

                if (cachedWeather != null && isCacheValid(cachedWeather.updated)) {
                    // Use cached data
                    val formattedTemp = formatTemperature(cachedWeather.temp)

                    updateWidgetViews(views, locationName, formattedTemp, cachedWeather.condition, cachedWeather.iconCode) // Default icon for cached data
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                    Log.d(TAG, "Using cached weather data")
                } else {
                    // Fetch fresh weather data
                    weatherNetworkRepository.getCurrentWeather(location).collect { weatherData ->
                        val locationState = if (location.state == null) "" else ", ${location.state}"
                        val locationName = "${location.name}$locationState"
                        Log.d(TAG, "Got weather data for location: $locationName")
                        val temperature = formatTemperature(weatherData.current.temp)
                        val condition =
                            weatherData.current.weather
                                .firstOrNull()
                                ?.main ?: "Unknown"
                        val iconCode =
                            weatherData.current.weather[0].icon

                        Log.d(TAG, "ICON CODE: $iconCode")

                        updateWidgetViews(views, locationName, temperature, condition, iconCode)
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                        Log.d(TAG, "Fetched fresh weather data: $temperature, $condition")

                        // Save to database for caching
                        val weatherEntity =
                            com.warbler.data.model.weather.WeatherDataSourceDto
                                .buildWeatherData(weatherData, locationName)
                        weatherDatabaseRepository.insertWeather(weatherEntity)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading weather data: ${e.message}")
                // Fallback to cached data or default values
                loadFallbackWeatherData(appWidgetManager, appWidgetId, views)
            }
        }
    }

    private fun updateWidgetViews(
        views: RemoteViews,
        location: String,
        temperature: String,
        condition: String,
        iconCode: String,
    ) {
        Log.d(TAG, "Updating widget views for location: $location, temperature: $temperature, condition: $condition")
        views.setTextViewText(R.id.widget_location, location)
        views.setTextViewText(R.id.widget_temperature, temperature)
        views.setTextViewText(R.id.widget_condition, condition)
        views.setImageViewResource(R.id.widget_weather_icon, iconCode.getIconForCondition)

        // Set last updated time
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        views.setTextViewText(R.id.widget_last_updated, "Updated ${timeFormat.format(java.util.Date())}")
    }

    private fun isCacheValid(lastUpdated: String): Boolean =
        try {
            val lastUpdatedInstant = java.time.Instant.parse(lastUpdated)
            val currentTime = java.time.Instant.now()
            val cacheValidityPeriod = java.time.Duration.ofMinutes(30)
            java.time.Duration.between(lastUpdatedInstant, currentTime) < cacheValidityPeriod
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing timestamp: ${e.message}")
            false // Consider cache invalid if we can't parse the timestamp
        }

    private fun loadFallbackWeatherData(
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
    ) {
        updateWidgetViews(views, "Weather Unavailable", "--°", "No Data", "02d")
        appWidgetManager.updateAppWidget(appWidgetId, views)
        Log.d(TAG, "Loaded fallback weather data")
    }

    private suspend fun formatTemperature(tempKelvin: Double): String {
        val temperatureUnit = DataPref.readIntDataStoreFlow(DataPref.TEMPERATURE_UNIT, dataStore).first()
        return when (temperatureUnit) {
            0 -> "${(tempKelvin - 273.15).toInt()}°C" // Celsius
            1 -> "${((tempKelvin - 273.15) * 9 / 5 + 32).toInt()}°F" // Fahrenheit
            2 -> "${tempKelvin.toInt()}K" // Kelvin
            else -> "${((tempKelvin - 273.15) * 9 / 5 + 32).toInt()}°F" // Default to Fahrenheit
        }
    }
}
