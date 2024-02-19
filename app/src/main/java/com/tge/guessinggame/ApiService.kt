package com.tge.guessinggame

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{word}")
    fun getDefinitionByWord(@Path("word") word: String): Call<List<DefinitionResponse>>
}

data class DefinitionResponse(
    val meanings: List<Meaning>
)

data class Meaning(
    val definitions: List<Definition>
)

data class Definition(
    val definition: String
)
