package com.learning.favoritesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learning.favoritesapp.model.fakeMovies
import com.learning.favoritesapp.ui.components.MovieCard
import com.learning.favoritesapp.ui.components.ToolbarComponent
import org.jetbrains.compose.ui.tooling.preview.Preview

class ListScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        ListScreenContent(
            openDetailsScreen = { movieId ->
                navigator.push(DetailsScreen(movieId = movieId))
            },
            openFavoritesScreen = {
                navigator.push(FavoritesScreen())
            }
        )
    }
}

@Composable
private fun ListScreenContent(
    openDetailsScreen: (movieId: String) -> Unit,
    openFavoritesScreen: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF09090B)
            )
    ) {
        ToolbarComponent(
            title = "Movies",
            openFavoritesScreen = openFavoritesScreen,
            showFavoritesButton = true,
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        LazyVerticalGrid(
            modifier = Modifier
                .padding(horizontal = 24.dp),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(fakeMovies) {
                MovieCard(
                    movie = it,
                    onClick = {
                        openDetailsScreen(it.id)
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
    }
}

@Preview
@Composable
private fun ListScreenPreview() {
    MaterialTheme {
        ListScreenContent(
            openDetailsScreen = {},
            openFavoritesScreen = {},
        )
    }
}