package com.warbler.data.database.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.warbler.data.model.weather.WeatherDataEntity

@Dao
interface WeatherDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: WeatherDataEntity)

    @Update
    fun updateWeather(weather: WeatherDataEntity)
}
