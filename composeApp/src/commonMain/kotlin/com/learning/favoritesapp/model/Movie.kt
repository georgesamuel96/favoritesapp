package com.learning.favoritesapp.model

data class Movie(
    val id: String,
    val title: String,
    val genre: String,
    val year: String,
    val duration: String,
    val rating: Double,
    val posterUrl: String,
    val synopsis: String,
)