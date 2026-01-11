package com.learning.favoritesapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.learning.favoritesapp.model.Movie
import com.learning.favoritesapp.model.fakeMovies
import favoritesapp.composeapp.generated.resources.Res
import favoritesapp.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(color = Color(0xFF18181B))
            .border(width = 1.dp, color = Color(0xFF71717B), shape = RoundedCornerShape(24.dp))
            .clickable {
                onClick()
            }
    ) {
        AsyncImage(
            model = movie.posterUrl,
            modifier = Modifier.fillMaxWidth()
                .height(244.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onState = {state ->
                when(state){
                    is AsyncImagePainter.State.Loading -> {
                    }
                    is AsyncImagePainter.State.Success -> {
                    }
                    is AsyncImagePainter.State.Error -> {
                    }
                    else -> {}
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier,
                text = movie.title,
                style = TextStyle(
                    color = Color.White,
                    fontWeight = FontWeight.W700,
                    fontSize = 18.sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.year,
                    style = TextStyle(
                        color = Color(0xFF71717B),
                        fontWeight = FontWeight.W400
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_star),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )

                    Text(
                        text = "${movie.rating}",
                        style = TextStyle(
                            color = Color(0xFFFFB900),
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MovieCardPreview() {
    MaterialTheme {
        MovieCard(fakeMovies[0], onClick = {},)
    }
}