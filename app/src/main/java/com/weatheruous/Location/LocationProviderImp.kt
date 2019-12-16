package com.weatheruous.Location

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*

private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1

class LocationProviderImp(application: Application) : LocationProvider, Application() {
    private val context = application.applicationContext

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationJob = Job()
    private var locationScope = CoroutineScope(Dispatchers.Main + locationJob)

    init {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(application)

        locationScope.launch {
            createLocationRequest()
            getLastLocation()
        }

    }

    override suspend fun hasLocationChanged(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getLastLocation(): String {



        var stringbuilder = StringBuilder()
        var stringy = ""
        if (hasLocationPermission()) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                  println("Longitude is: ${location?.longitude}")
                    println("Latitude is: ${location?.latitude}")
                }
        }
        return stringbuilder.toString()
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    }

}