package com.learning.favoritesapp.ui.screens.list

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learning.favoritesapp.model.Movie
import com.learning.favoritesapp.ui.components.MovieCard
import com.learning.favoritesapp.ui.components.ToolbarComponent
import com.learning.favoritesapp.ui.screens.details.DetailsScreen
import com.learning.favoritesapp.ui.screens.favorites.FavoritesScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.koin.getScreenModel

class ListScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ListViewModel>()
        val listUiState by screenModel.uiState.collectAsState()

        ListScreenContent(
            listUiState = listUiState,
            openDetailsScreen = { movie ->
                navigator.push(DetailsScreen(movie = movie))
            },
            openFavoritesScreen = {
                navigator.push(FavoritesScreen())
            }
        )
    }
}

@Composable
private fun ListScreenContent(
    listUiState: ListUiState,
    openDetailsScreen: (movie: Movie) -> Unit,
    openFavoritesScreen: () -> Unit,
) {
    var showErrorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF09090B)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ToolbarComponent(
            title = "Movies",
            openFavoritesScreen = openFavoritesScreen,
            showFavoritesButton = true,
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if(listUiState.error.isNullOrEmpty().not()) {
            showErrorDialog = true
        }

        if(showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text("Error") },
                text = { Text(listUiState.error?: "Unknown error") },
                confirmButton = {
                    TextButton(onClick = { showErrorDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if(listUiState.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(listUiState.movies) {
                    MovieCard(
                        movie = it,
                        onClick = {
                            openDetailsScreen(it)
                        }
                    )
                }
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
            listUiState = ListUiState(),
            openDetailsScreen = {},
            openFavoritesScreen = {},
        )
    }
}

@Preview
@Composable
private fun ListScreenErrorPreview() {
    MaterialTheme {
        ListScreenContent(
            listUiState = ListUiState(error = "Something went wrong"),
            openDetailsScreen = {},
            openFavoritesScreen = {},
        )
    }
}