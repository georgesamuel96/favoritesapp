package com.learning.favoritesapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MovieApi{
    private val apiKey = "d1105a4f50377780add11daec3756b67"
    private val baseUrl = "https://api.themoviedb.org/3"

    private val client = HttpClient {
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun getPopularMovies(page: Int = 1): MovieResponse {
        return client.get("$baseUrl/movie/popular") {
            parameter("api_key", apiKey)
            parameter("page", page)
        }.body()
    }
}