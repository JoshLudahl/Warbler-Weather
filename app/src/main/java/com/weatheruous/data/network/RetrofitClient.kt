package com.weatheruous.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private val contentType = "application/json".toMediaType()

    @OptIn(ExperimentalSerializationApi::class)
    fun getRetrofitWithBaseUrl(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory(contentType = contentType))
            .baseUrl(baseUrl)
            .build()
    }
}
