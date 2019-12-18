package com.weatheruous.Location

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay

private const val MY_PERMISSION_ACCESS_COARSE_LOCATION = 1
private var locations: Location? = null

class LocationProviderImp(application: Application) : LocationProvider, Application() {

    private val context = application.applicationContext
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)

    override suspend fun hasLocationChanged(): Boolean {
        TODO("not implemented")
        //  Might be restricting this
    }

    override suspend fun getLastLocation(): String {

        val builder: StringBuilder = StringBuilder()
        if (hasLocationPermission()) {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    locations = location

                    // built as 'latitude,longitude'
                    builder.append(location?.latitude.toString())
                        .append(",")
                        .append(location?.longitude.toString())
                }

            //  Not sure if I can get away without having this delay, seems egregious in this capacity.
            delay(500)
            println("Location: ${locations?.latitude} + ${locations?.longitude}")

            return builder.toString()
        }
        else {
            TODO("Add permission request, descriptive error toast for now.")
            Toast.makeText(applicationContext, "Unable to get location.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}