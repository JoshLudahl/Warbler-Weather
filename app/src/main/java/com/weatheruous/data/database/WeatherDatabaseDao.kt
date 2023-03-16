package com.weatheruous.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.weatheruous.data.WeatherUrls

@Dao
interface WeatherDatabaseDao {

    @Insert
    fun insert(weatherUrls: WeatherUrls)

    @Update
    fun update(weatherUrls: WeatherUrls)

    @Query("SELECT * FROM location_information")
    fun getWeatherUrls(): WeatherUrls

    @Query("DELETE FROM location_information")
    fun clear()
}
