package com.learning.favoritesapp.data.local

import app.cash.sqldelight.db.SqlDriver
import com.learning.favoritesapp.db.FavoritesDatabase

expect fun createSqlDriver(): SqlDriver

fun createDatabase(): FavoritesDatabase {
    return FavoritesDatabase(createSqlDriver())
}