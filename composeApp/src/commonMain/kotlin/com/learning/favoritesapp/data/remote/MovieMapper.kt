package com.learning.favoritesapp.data.remote

import com.learning.favoritesapp.model.Movie

object MovieMapper {
    fun mapToMovie(movieResponse: MovieDto): Movie {
        return Movie(
            id = "${movieResponse.id}",
            title = movieResponse.title,
            genre = "Movie",
            year = movieResponse.releaseDate?.substring(0, 4)?: "Unknown",
            duration = "N/A",
            rating = movieResponse.voteAverage,
            posterUrl = movieResponse.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
            synopsis = movieResponse.overview
        )
    }
}