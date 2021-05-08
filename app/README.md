 
# URLs for api.weather.gov

|Identity | URL |
| ------ | ------ |
| api documentation | [https://www.weather.gov/documentation/services-web-api](https://www.weather.gov/documentation/services-web-api)|
| api status | [api.weather.gov](https://api.weather.gov) |
| points | [https://api.weather.gov/points/{points}](https://api.weather.gov/points/) |


#####  Get location (if location access is allowed)
* This will attempt to get the location of the user
* It will check the database for a location and update the long/lat
* It will refresh the weather view


##### Ask for a zip code if location isn't allowed
* This will use a zip code to find the long lat of a city
* It will check the database for a location and update the long/lat
* It will refresh the weather view

##### 
Currently crashes until location permission is implemented. :)