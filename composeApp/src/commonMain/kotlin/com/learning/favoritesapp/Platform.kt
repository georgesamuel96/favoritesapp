package com.learning.favoritesapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform