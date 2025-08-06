package com.warbler.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationService
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val fusedLocationClient: FusedLocationProviderClient by lazy {
            LocationServices.getFusedLocationProviderClient(context)
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

        suspend fun getCurrentLocation(): Location? =
            suspendCancellableCoroutine { continuation ->
                if (!hasLocationPermission()) {
                    continuation.resumeWithException(SecurityException("Location permission not granted"))
                    return@suspendCancellableCoroutine
                }

                val cancellationTokenSource = CancellationTokenSource()

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }

                try {
                    fusedLocationClient
                        .getCurrentLocation(
                            Priority.PRIORITY_HIGH_ACCURACY,
                            cancellationTokenSource.token,
                        ).addOnSuccessListener { location ->
                            continuation.resume(location)
                        }.addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(e)
                }
            }
    }
