package com.learning.favoritesapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>,
)

@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("release_date")
    val releaseDate: String?,
    @SerialName("vote_average")
    val voteAverage: Double,
    val overview: String,
)