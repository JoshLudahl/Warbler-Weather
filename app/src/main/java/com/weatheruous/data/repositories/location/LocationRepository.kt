package com.weatheruous.data.repositories.location

import com.weatheruous.data.network.locations.LocationAPI

class LocationRepository {

    fun getLocationByQuery(query: String) = LocationAPI.locationApiService.getLocations(query)

}
