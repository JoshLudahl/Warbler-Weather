package com.weatheruous.utilities

sealed class Resource<out T> {
    class Success<T>(val data: T) : Resource<T>()
    data class Error(
        val exception: Exception? = null,
        val message: String? = null
    ) : Resource<Nothing>()

    object Loading : Resource<Nothing>()
}
