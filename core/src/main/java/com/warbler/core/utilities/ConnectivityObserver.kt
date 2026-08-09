package com.warbler.core.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available,
        Unavailable,
        Losing,
        Lost,
    }
}

@Singleton
class NetworkConnectivityObserver
    @Inject
    constructor(
        private val context: Context,
    ) : ConnectivityObserver {
        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        override fun observe(): Flow<ConnectivityObserver.Status> =
            callbackFlow {
                val callback =
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            launch { send(ConnectivityObserver.Status.Available) }
                        }

                        override fun onLosing(
                            network: Network,
                            maxMsToLive: Int,
                        ) {
                            super.onLosing(network, maxMsToLive)
                            launch { send(ConnectivityObserver.Status.Losing) }
                        }

                        override fun onLost(network: Network) {
                            super.onLost(network)
                            launch { send(ConnectivityObserver.Status.Lost) }
                        }

                        override fun onUnavailable() {
                            super.onUnavailable()
                            launch { send(ConnectivityObserver.Status.Unavailable) }
                        }
                    }

                val networkRequest =
                    NetworkRequest
                        .Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                connectivityManager.registerNetworkCallback(networkRequest, callback)

                // Initial check
                val isOnline =
                    connectivityManager
                        .getNetworkCapabilities(connectivityManager.activeNetwork)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

                if (isOnline) {
                    send(ConnectivityObserver.Status.Available)
                } else {
                    send(ConnectivityObserver.Status.Unavailable)
                }

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }.distinctUntilChanged()
    }
