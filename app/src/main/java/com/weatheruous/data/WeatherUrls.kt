package com.weatheruous.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_information")
data class WeatherUrls(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "grid_points")
    var gridpoints: String = "107,99",

    @ColumnInfo(name = "system_of_measurement")
    var scale: String = "imperial",

    @ColumnInfo(name = "lat_long")
    var location: String = "45.45945945945946,-122.78933059959456"

)
