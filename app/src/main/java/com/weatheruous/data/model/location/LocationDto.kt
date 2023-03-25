package com.weatheruous.data.model.location

class LocationDto {
    fun locationDataSourceToLocationEntity(location: LocationItem): LocationEntity {
        return LocationEntity(
            lat = location.lat,
            lon = location.lon,
            name = location.name,
            state = location.state
        )
    }
}
