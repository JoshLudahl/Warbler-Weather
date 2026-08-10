package com.warbler.core.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    /** [Available] means a network is connected *and* validated as reaching the internet. */
    enum class Status {
        Available,
        Unavailable,
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
                // Networks that are currently able to reach the internet. Callbacks fire per
                // network, so a device on wifi + cellular must not be reported as offline just
                // because one of them dropped. Only touched from the callback thread.
                val usableNetworks = mutableSetOf<Network>()

                fun emitStatus() {
                    trySend(
                        if (usableNetworks.isNotEmpty()) {
                            ConnectivityObserver.Status.Available
                        } else {
                            ConnectivityObserver.Status.Unavailable
                        },
                    )
                }

                val callback =
                    object : ConnectivityManager.NetworkCallback() {
                        // onAvailable fires as soon as a network connects, which is before it is
                        // known to actually reach the internet. Reporting Available here makes
                        // the first request after a reconnect fail, so wait for validation in
                        // onCapabilitiesChanged instead.
                        override fun onCapabilitiesChanged(
                            network: Network,
                            networkCapabilities: NetworkCapabilities,
                        ) {
                            super.onCapabilitiesChanged(network, networkCapabilities)
                            if (networkCapabilities.isUsable) {
                                usableNetworks.add(network)
                            } else {
                                usableNetworks.remove(network)
                            }
                            emitStatus()
                        }

                        override fun onLost(network: Network) {
                            super.onLost(network)
                            usableNetworks.remove(network)
                            emitStatus()
                        }

                        override fun onUnavailable() {
                            super.onUnavailable()
                            emitStatus()
                        }
                    }

                val networkRequest =
                    NetworkRequest
                        .Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                connectivityManager.registerNetworkCallback(networkRequest, callback)

                // Initial check
                val activeNetwork = connectivityManager.activeNetwork
                if (activeNetwork != null &&
                    connectivityManager.getNetworkCapabilities(activeNetwork)?.isUsable == true
                ) {
                    usableNetworks.add(activeNetwork)
                }
                emitStatus()

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }
                // Emit from the callback thread without suspending so statuses can never be
                // delivered out of order; only the latest one matters.
                .conflate()
                .distinctUntilChanged()

        private val NetworkCapabilities.isUsable: Boolean
            get() =
                hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
