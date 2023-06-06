package com.example.carbrief.routes

object ApiRoutes {
    private const val BASE_URL = "https://ivc-system-service.onrender.com"
    private const val USERS = "$BASE_URL/users"
    const val PROFILE = "$USERS/profile"
    const val USERS_CREATE = "$USERS/create"

    private const val AUTH = "$BASE_URL/auth"
    const val LOGIN = "$AUTH/login"
    const val REFRESH = "$AUTH/refresh"
}