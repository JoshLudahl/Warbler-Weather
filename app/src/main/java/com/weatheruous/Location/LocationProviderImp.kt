package com.weatheruous.Location

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay

private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1

class LocationProviderImp(application: Application) : LocationProvider, Application() {

    private val context = application.applicationContext
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(application)
    }

    override suspend fun hasLocationChanged(): Boolean {
        TODO("not implemented")
        //  Might be restricting this
    }

    override suspend fun getLastLocation(): String {

        var builder: StringBuilder = StringBuilder()
        if (hasLocationPermission()) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->

                    builder.append(location?.latitude.toString())
                        .append(",")
                        .append(location?.longitude.toString())
                }
            //  Not sure if I can get away without having this delay, seems egregious in this capacity.
            delay(1000)
            return builder.toString()
        }
        else {
            TODO("Add permission request")
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}