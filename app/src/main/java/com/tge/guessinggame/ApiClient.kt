package com.tge.guessinggame

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val hintApi = "https://api.dictionaryapi.dev/api/v2/entries/en/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(hintApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}