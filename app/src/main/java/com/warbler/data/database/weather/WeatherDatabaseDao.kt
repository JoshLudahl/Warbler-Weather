package com.warbler.data.database.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.warbler.data.model.weather.WeatherDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherDataEntity)

    @Update
    fun updateWeather(weather: WeatherDataEntity)

    @Query("SELECT * FROM weather_table ORDER BY updated DESC LIMIT 1")
    fun getCurrentWeather(): Flow<WeatherDataEntity?>

    @Query("SELECT * FROM weather_table WHERE lat = :lat AND lon = :lon LIMIT 1")
    fun getWeatherByLocation(
        lat: Double,
        lon: Double,
    ): Flow<WeatherDataEntity?>

    @Query("SELECT * FROM weather_table ORDER BY updated DESC LIMIT 1")
    suspend fun getCurrentWeatherSync(): WeatherDataEntity?
}
