package com.learning.favoritesapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.learning.favoritesapp.model.Movie
import com.learning.favoritesapp.model.fakeMovies
import favoritesapp.composeapp.generated.resources.Res
import favoritesapp.composeapp.generated.resources.ic_back
import favoritesapp.composeapp.generated.resources.ic_calender
import favoritesapp.composeapp.generated.resources.ic_save
import favoritesapp.composeapp.generated.resources.ic_star
import favoritesapp.composeapp.generated.resources.ic_time
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

class DetailsScreen(
    private val movieId: String,
) : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val movie = fakeMovies.find { it.id == movieId }

        movie?.let {
            DetailsScreenContent(
                movie = movie, goBack = {
                    navigator.pop()
                })
        }
    }
}

@Composable
private fun DetailsScreenContent(
    movie: Movie,
    goBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(
                color = Color(0xFF09090B)
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .weight(1f)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        modifier = Modifier.fillMaxWidth().height(400.dp),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .height(400.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF09090B)
                                    ),
                                    startY = 200f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .align(Alignment.BottomStart)
                            .background(
                                color = Color(0x33C4B4FF), shape = MaterialTheme.shapes.medium
                            ).border(
                                width = 1.dp,
                                color = Color(0x4DC4B4FF),
                                shape = MaterialTheme.shapes.medium
                            ).padding(horizontal = 12.dp, vertical = 4.dp),
                        text = movie.genre,
                        style = TextStyle(
                            color = Color(0xFFC4B4FF)
                        )
                    )

                    IconButton(
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(top = 32.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(color = Color(0x66000000), shape = CircleShape)
                            .border(width = 1.dp, color = Color(0x1AFFFFFF), shape = CircleShape),
                        onClick = {
                            goBack()
                        },
                    ){
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(), text = movie.title, style = TextStyle(
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.W700,
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_calender),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                            Text(
                                text = "${movie.year}",
                                style = TextStyle(
                                    color = Color(0xFFD4D4D8), fontWeight = FontWeight.W400
                                ),
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_time),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                            Text(
                                text = movie.duration,
                                style = TextStyle(
                                    color = Color(0xFFD4D4D8), fontWeight = FontWeight.W400
                                ),
                            )
                        }
                        Row(
                            modifier = Modifier.background(
                                color = Color(0x1AFFB900),
                                shape = RoundedCornerShape(10.dp),
                            ).border(
                                width = 1.dp,
                                color = Color(0x33FFB900),
                                shape = MaterialTheme.shapes.medium
                            ).padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                            Text(
                                text = "${movie.rating}",
                                style = TextStyle(
                                    color = Color(0xFFFFB900), fontWeight = FontWeight.W700
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = "Synopsis",
                        style = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.W600
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = movie.synopsis,
                        style = TextStyle(
                            color = Color(0xFF9F9FA9),
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
        Button(
            modifier = Modifier.padding(24.dp),
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff7F22FE)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_save),
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = "Add to Favorites"
                )
            }
        }
    }
}

@Preview
@Composable
private fun DetailsScreenPreview() {
    MaterialTheme {
        DetailsScreenContent(
            movie = fakeMovies[0],
            goBack = {},
        )
    }
}