package com.example.carbrief.routes

object ApiRoutes {
    private const val BASE_URL = "http://10.0.2.2:8000"
    private const val USERS = "$BASE_URL/users"
    const val USERS_CREATE = "$USERS/create"

    private const val AUTH = "$BASE_URL/auth"
    const val LOGIN = "$AUTH/login"
    const val REFRESH = "$AUTH/refresh"
}