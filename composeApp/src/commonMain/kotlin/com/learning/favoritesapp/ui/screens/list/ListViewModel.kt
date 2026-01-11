package com.learning.favoritesapp.ui.screens.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.learning.favoritesapp.data.repository.MovieRepository
import com.learning.favoritesapp.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListViewModel(
    private val repository: MovieRepository
): ScreenModel {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        screenModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }
            try {
                val movies = repository.getPopularMovies()
                _uiState.update { state ->
                    state.copy(movies = movies, isLoading = false, error = null)
                }
            } catch (e: Exception) {
                println("Error loading movies: ${e.message}")  // Add this line

                _uiState.update { state ->
                    state.copy(error = e.message ?: "Unknown error", isLoading = false)
                }
            }
        }
    }
}

data class ListUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)