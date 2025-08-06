package com.warbler.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.warbler.data.database.location.LocationDao
import com.warbler.data.database.weather.WeatherDatabase
import com.warbler.data.database.weather.WeatherDatabaseDao
import com.warbler.data.repositories.location.LocationRepository
import com.warbler.data.repositories.weather.WeatherDatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val MIGRATION_1_2 =
        object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE weather_table ADD COLUMN iconCode TEXT NOT NULL DEFAULT '02d'")
            }
        }

    @Singleton
    @Provides
    fun providesWeatherDatabase(
        @ApplicationContext context: Context,
    ) = Room
        .databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database",
        ).addMigrations(MIGRATION_1_2)
        .build()

    @Singleton
    @Provides
    fun providesWeatherDatabaseDao(database: WeatherDatabase) = database.weatherDao()

    @Singleton
    @Provides
    fun providesWeatherDatabaseRepository(weatherDatabaseDao: WeatherDatabaseDao) = WeatherDatabaseRepository(weatherDatabaseDao)

    @Singleton
    @Provides
    fun providesLocationDatabaseDao(database: WeatherDatabase) = database.locationDao()

    @Singleton
    @Provides
    fun providesLocationRepository(locationDao: LocationDao) = LocationRepository(locationDao)

    @Singleton
    @Provides
    fun providesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Singleton
    @Provides
    fun providesContext(
        @ApplicationContext context: Context,
    ) = context
}
