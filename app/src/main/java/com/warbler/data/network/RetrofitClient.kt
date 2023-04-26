package com.warbler.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun getRetrofitWithBaseUrl(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType = contentType))
            .baseUrl(baseUrl)
            .build()
    }
}
