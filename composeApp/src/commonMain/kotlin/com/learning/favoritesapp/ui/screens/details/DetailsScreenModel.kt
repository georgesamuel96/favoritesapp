package com.learning.favoritesapp.ui.screens.details

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.learning.favoritesapp.data.repository.FavoritesRepository
import com.learning.favoritesapp.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailsScreenModel(
    private val movie: Movie,
    private val repository: FavoritesRepository
) : ScreenModel {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    init {
        checkIfFavorite()
    }

    private fun checkIfFavorite() {
        screenModelScope.launch {
            repository.isFavorite(movie.id).collect { isFav ->
                println("checkIfFavorite: $isFav")
                _isFavorite.update { isFav }
                println("_isFavorite.value: ${_isFavorite.value}")
            }
        }
    }

    fun toggleFavorite() {
        screenModelScope.launch {
            println("toggleFavorite")
            println("_isFavorite.value: ${_isFavorite.value}")
            if(_isFavorite.value) {
                repository.removeFavorite(movie.id)
            } else {
                repository.addFavorite(movie)
            }
        }
    }
}