[![Android CI](https://github.com/JoshLudahl/Warbler-Weather/actions/workflows/android.yml/badge.svg)](https://github.com/JoshLudahl/Warbler-Weather/actions/workflows/android.yml)

# Warbler Weather
[About](https://softklass.com/weatheruous/)
[Privacy Policy](https://softklass.com/privacy-policy/weatheruous.html)

## A simple app for weather
The purpose of this app is to offer an unbiased view of weather data without all the fuss (ads, tracking, data mining). 
The app is themed after the Yellow-rumped Warbler bird.

### Libraries used
* Written in [Kotlin](https://kotlinlang.org/)
  * Uses Kotlin [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
* [Jetpack Navigation](https://developer.android.com/guide/navigation)
* [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
* [Flow](https://developer.android.com/kotlin/flow)
* [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
* [DataBinding](https://developer.android.com/topic/libraries/data-binding)
* [Retrofit](https://square.github.io/retrofit/) - An HTTP client for Android
* [Room Database](https://developer.android.com/training/data-storage/room)
* [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
* Dependency Injection using [Dagger Hilt](https://dagger.dev/hilt/) for [Android](https://developer.android.com/training/dependency-injection/hilt-android)
* [Notifications](https://developer.android.com/develop/ui/views/notifications)
* [MVVM](https://developer.android.com/topic/architecture)
* [Material Design Components for Android](https://github.com/material-components/material-components-android)
* Testing includes
  * [Mockito](https://developer.android.com/training/testing/local-tests) for unit testing
  * [Espresso](https://developer.android.com/training/testing/espresso) for UI testing

### Features
* City Finder
* Location Service
* Favorites
* [Weather Icons](https://erikflowers.github.io/weather-icons/)
* Current Weather data from [Open Weather Map](https://openweathermap.org/)
  * Forecast
  * Hourly
  * Air Quality

## Architecture
This app uses [***MVVM (Model View View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

![](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)

## Setup
In the ```build.gradle```, you will find an associated weather API key assigned, so you will want to
visit [Open Weather Map](https://openweathermap.org/api) and sign up to get an API key. 
Once you have an API key, place it in your global ```gradle.properties``` file or in your environment. 
It should look like this, replacing ```<api-key>``` with the actual key:
```WEATHER_API_KEY=<api-key>```

## Tests
There are UI tests setup to run Espresso tests. Unit tests are also configured and uses Mockito.

## CI
This project is configured to run Github Actions and uses Dependabot for dependency updates.
The GitHub Actions will run both unit tests and UI tests on master, or pull requests.

## Contributing
Since this project is open source, contributing is encouraged.


## Attributions
Color theme by [Color Hunt](https://colorhunt.co/palette/222831393e46ffd369eeeeee), chosen to emulate
the color of the Yellow-rumped Warbler bird.
```
MIT License

Copyright (c) 2023 Josh Ludahl

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```