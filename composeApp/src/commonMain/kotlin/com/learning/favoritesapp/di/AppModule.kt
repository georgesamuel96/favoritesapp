package com.learning.favoritesapp.di

import com.learning.favoritesapp.data.local.createDatabase
import com.learning.favoritesapp.data.remote.MovieApi
import com.learning.favoritesapp.data.repository.FavoritesRepository
import com.learning.favoritesapp.data.repository.MovieRepository
import com.learning.favoritesapp.ui.screens.favorites.FavoritesScreenModel
import com.learning.favoritesapp.ui.screens.list.ListViewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single { createDatabase() }

    // API
    single { MovieApi() }

    // Repositories
    single { MovieRepository(get()) }
    single { FavoritesRepository(get()) }

    // ScreenModels
    factory { ListViewModel(get()) }
    factory { FavoritesScreenModel(get()) }
}