package com.warbler.utilities

sealed class Resource<out T> {
    data class Error(
        val exception: Exception? = null,
        val message: String? = null,
    ) : Resource<Nothing>()

    data object Loading : Resource<Nothing>()

    class Success<T>(val data: T) : Resource<T>()
}
