package com.learning.favoritesapp.data.repository

import com.learning.favoritesapp.data.remote.MovieApi
import com.learning.favoritesapp.data.remote.MovieMapper
import com.learning.favoritesapp.model.Movie

class MovieRepository{
    private val api = MovieApi()

    suspend fun getPopularMovies(): List<Movie> {
        val response = api.getPopularMovies()

        return response.results.map { MovieMapper.mapToMovie(it) }
    }
}