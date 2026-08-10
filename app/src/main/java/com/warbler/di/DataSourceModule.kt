package com.warbler.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.warbler.core.data.database.location.LocationDao
import com.warbler.core.data.repositories.location.LocationRepository
import com.warbler.core.utilities.ConnectivityObserver
import com.warbler.core.utilities.NetworkConnectivityObserver
import com.warbler.data.database.weather.WeatherDatabase
import com.warbler.data.database.weather.WeatherDatabaseDao
import com.warbler.data.repositories.weather.WeatherDatabaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindsConnectivityObserver(
        networkConnectivityObserver: NetworkConnectivityObserver,
    ): ConnectivityObserver

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE weather_table ADD COLUMN iconCode TEXT NOT NULL DEFAULT '02d'")
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        "CREATE TABLE IF NOT EXISTS `weather_cache` (`lat` REAL NOT NULL, `lon` REAL NOT NULL, `json` TEXT NOT NULL, `lastUpdated` INTEGER NOT NULL, PRIMARY KEY(`lat`, `lon`))",
                    )
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
                "weather_database_v2",
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
}
