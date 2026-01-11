package com.learning.favoritesapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.learning.favoritesapp.di.appModule
import com.learning.favoritesapp.ui.screens.list.ListScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule)
    }) {
        MaterialTheme {
            Navigator(ListScreen())
        }
    }
}