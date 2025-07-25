package io.jadu.remindmex.remindMe.data.api

import io.jadu.remindmex.remindMe.domain.repository.GeminiApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiApiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    fun create(): GeminiApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}

