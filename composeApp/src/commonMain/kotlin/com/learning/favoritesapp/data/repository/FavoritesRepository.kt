package com.learning.favoritesapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.learning.favoritesapp.db.FavoriteMovie
import com.learning.favoritesapp.db.FavoritesDatabase
import com.learning.favoritesapp.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val database: FavoritesDatabase
) {
    private val queries = database.favoriteMovieQueries

    fun getAllFavorites(): Flow<List<Movie>> {
        return queries.getAllFavorites()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { favorites ->
                favorites.map { it.toMovie() }
            }
    }

    fun addFavorite(movie: Movie) {
        queries.insertFavorite(
            id = movie.id,
            title = movie.title,
            genre = movie.genre,
            year = movie.year,
            duration = movie.duration,
            rating = movie.rating,
            posterUrl = movie.posterUrl,
            synopsis = movie.synopsis
        )
    }

    suspend fun removeFavorite(movieId: String) {
        queries.deleteFavorite(movieId)
    }

    fun isFavorite(movieId: String): Flow<Boolean> {
        return queries.isFavorite(movieId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.firstOrNull() != null && it.first() > 0 }
    }
}

private fun FavoriteMovie.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        genre = genre,
        year = year,
        duration = duration,
        rating = rating,
        posterUrl = posterUrl,
        synopsis = synopsis
    )
}