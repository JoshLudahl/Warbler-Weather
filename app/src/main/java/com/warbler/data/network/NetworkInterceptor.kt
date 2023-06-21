package com.warbler.data.network

import android.content.Context
import java.net.UnknownHostException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkInterceptor(
    private val context: Context
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (ConnectionHandler.isOnline(context)) {
            return chain.proceed(chain.request())
        }
        throw UnknownHostException("No internet available.")
    }
}
