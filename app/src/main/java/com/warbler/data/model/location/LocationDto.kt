package com.warbler.data.model.location

object LocationDto {
    fun locationDataSourceToLocationEntity(location: LocationDataSource): LocationEntity =
        LocationEntity(
            country = location.country,
            lat = location.lat,
            lon = location.lon,
            name = location.name,
            state = location.state,
        )
}
