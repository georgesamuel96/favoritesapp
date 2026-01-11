package com.learning.favoritesapp.ui.screens.favorites

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.learning.favoritesapp.data.repository.FavoritesRepository
import com.learning.favoritesapp.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesScreenModel(
    private val repository: FavoritesRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        screenModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                _uiState.update { it.copy(
                    favorites = favorites,
                    isLoading = false
                )}
            }
        }
    }
}

data class FavoritesUiState(
    val favorites: List<Movie> = emptyList(),
    val isLoading: Boolean = true,
)