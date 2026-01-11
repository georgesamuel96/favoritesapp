package com.learning.favoritesapp.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.learning.favoritesapp.db.FavoritesDatabase

lateinit var appContext: android.content.Context

actual fun createSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(FavoritesDatabase.Schema, appContext, "favorites.db")
}