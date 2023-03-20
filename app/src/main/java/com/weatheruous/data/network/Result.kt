package com.weatheruous.data.network

sealed class Result {
    data class Success<T>(val data: T) : Result()
    data class Error(val exception: Exception) : Result()
    data class Loading(val isLoading: Boolean) : Result()
}
