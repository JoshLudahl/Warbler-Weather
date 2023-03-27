package com.weatheruous.data.network

sealed class Resource {
    class Success<T>(val data: T) : Resource()
    data class Error(
        val exception: Exception? = null,
        val message: String? = null
    ) : Resource()

    object Loading : Resource()
}
