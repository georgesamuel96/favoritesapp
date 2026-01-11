package com.learning.favoritesapp.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.learning.favoritesapp.db.FavoritesDatabase

actual fun createSqlDriver(): SqlDriver {
    return NativeSqliteDriver(FavoritesDatabase.Schema, "favorites.db")
}