package com.warbler.core.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationService
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val fusedLocationClient: FusedLocationProviderClient by lazy {
            LocationServices.getFusedLocationProviderClient(context)
        }

        private fun isGooglePlayServicesAvailable(): Boolean {
            val googleApiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
            return resultCode == ConnectionResult.SUCCESS
        }

        fun hasLocationPermission(): Boolean =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED

        private fun isFineLocationGranted(): Boolean =
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

        @SuppressLint("MissingPermission")
        suspend fun getLastLocation(): Location? =
            suspendCancellableCoroutine { continuation ->
                if (!hasLocationPermission()) {
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                if (!isGooglePlayServicesAvailable()) {
                    Log.e("LocationService", "Google Play Services not available")
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        Log.d("LocationService", "getLastLocation: success $location")
                        continuation.resume(location)
                    }.addOnFailureListener { exception ->
                        Log.d("LocationService", "getLastLocation: failure ${exception.message}")
                        continuation.resume(null)
                    }
            }

        @SuppressLint("MissingPermission")
        suspend fun getCurrentLocation(): Location? =
            suspendCancellableCoroutine { continuation ->
                if (!hasLocationPermission()) {
                    continuation.resumeWithException(SecurityException("Location permission not granted"))
                    return@suspendCancellableCoroutine
                }

                if (!isGooglePlayServicesAvailable()) {
                    Log.e("LocationService", "Google Play Services not available")
                    continuation.resume(null)
                    return@suspendCancellableCoroutine
                }

                val cancellationTokenSource = CancellationTokenSource()

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }

                val priority =
                    if (isFineLocationGranted()) {
                        Priority.PRIORITY_HIGH_ACCURACY
                    } else {
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }

                Log.d("LocationService", "getCurrentLocation: starting with priority $priority")

                try {
                    fusedLocationClient
                        .getCurrentLocation(
                            priority,
                            cancellationTokenSource.token,
                        ).addOnSuccessListener { location ->
                            Log.d("LocationService", "getCurrentLocation: success $location")
                            continuation.resume(location)
                        }.addOnFailureListener { exception ->
                            Log.d("LocationService", "getCurrentLocation: failure ${exception.message}")
                            continuation.resume(null) // Resume with null to allow fallback
                        }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(e)
                } catch (e: Exception) {
                    Log.d("LocationService", "getCurrentLocation: exception ${e.message}")
                    continuation.resume(null)
                }
            }
    }
