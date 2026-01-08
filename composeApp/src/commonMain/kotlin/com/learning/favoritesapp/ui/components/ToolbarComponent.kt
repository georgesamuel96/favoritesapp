package com.learning.favoritesapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import favoritesapp.composeapp.generated.resources.Res
import favoritesapp.composeapp.generated.resources.ic_back
import favoritesapp.composeapp.generated.resources.ic_save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ToolbarComponent(
    title: String,
    showBackButton: Boolean = false,
    goBack: () -> Unit = {},
    showFavoritesButton: Boolean = false,
    openFavoritesScreen: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    if (showBackButton) {
                        IconButton(onClick = goBack) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_back),
                                contentDescription = null,
                                tint = Color.Unspecified,
                            )
                        }
                    }
                    Text(
                        text = title,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.W700,
                        )
                    )
                }

                if (showFavoritesButton) {
                    IconButton(onClick = openFavoritesScreen) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_save),
                            contentDescription = null,
                            tint = Color.Unspecified,
                        )
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), color = Color(0xFF9F9FA9))
    }
}

@Preview
@Composable
private fun ToolbarComponentPreview() {
    MaterialTheme {
        ToolbarComponent(
            title = "Movies",
            openFavoritesScreen = {},
        )
    }
}