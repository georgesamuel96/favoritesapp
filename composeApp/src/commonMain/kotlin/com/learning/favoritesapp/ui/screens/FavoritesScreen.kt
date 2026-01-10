package com.learning.favoritesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.learning.favoritesapp.model.Movie
import com.learning.favoritesapp.model.fakeMovies
import com.learning.favoritesapp.ui.components.MovieCard
import com.learning.favoritesapp.ui.components.ToolbarComponent
import favoritesapp.composeapp.generated.resources.Res
import favoritesapp.composeapp.generated.resources.ic_save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class FavoritesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        FavoritesScreenContent(
            openDetailsScreen = {
//                navigator.push(DetailsScreen(it))
            },
            goBack = {
            navigator.pop()
        })
    }
}

@Composable
private fun FavoritesScreenContent(
    goBack: () -> Unit,
    openDetailsScreen: (movieId: String) -> Unit,
) {
    var emptyList = false

    Column(
        modifier = Modifier.fillMaxSize().background(color = Color(0xFF09090B)),
    ) {
        ToolbarComponent(
            title = "Favorites",
            showBackButton = true,
            goBack = goBack,
        )

        if (emptyList) {
            EmptyListComponent(goBack = goBack)
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            MovieListComponent(movieList = fakeMovies.subList(0, 3), openDetailsScreen = openDetailsScreen)
        }
    }
}

@Composable
private fun EmptyListComponent(
    goBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(96.dp)
                .clip(CircleShape)
                .background(color = Color(0xFF18181B))
                .border(1.dp, color = Color(0xFF27272A), shape = CircleShape)
        ) {
            Icon(
                modifier = Modifier.size(40.dp).align(Alignment.Center),
                painter = painterResource(Res.drawable.ic_save),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "No favorites yet",
                style = TextStyle(
                    color = Color(0xFFE4E4E7),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                )
            )
            Text(
                text = "Start exploring movies and add them to your favorites list to see them here.",
                style = TextStyle(
                    color = Color(0xFF71717B),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    textAlign = TextAlign.Center,
                )
            )
        }
        Button(
            onClick = goBack,
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ){
            Text(
                text = "Explore Movies",
            )
        }
    }
}

@Composable
private fun MovieListComponent(movieList: List<Movie>, openDetailsScreen: (movieId: String) -> Unit,) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(horizontal = 24.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(movieList) {
            MovieCard(
                movie = it,
                onClick = {
                    openDetailsScreen(it.id)
                }
            )
        }
    }
}


@Preview
@Composable
private fun FavoritesScreenPreview() {
    MaterialTheme {
        FavoritesScreenContent(goBack = {}, openDetailsScreen = {})
    }
}