package com.weatheruous.data.model.location

object LocationDto {
    fun locationDataSourceToLocationEntity(location: LocationDataSource): LocationEntity {
        return LocationEntity(
            lat = location.lat,
            lon = location.lon,
            name = location.name,
            state = location.state
        )
    }
}
