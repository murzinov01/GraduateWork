package com.example.carbrief.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileModel(
    var username: String? = null,
    var email: String? = null,
)

@Serializable
data class UserCreateModel(
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
)

@Serializable
data class UserLoginModel(
    var username: String? = null,
    var password: String? = null,
)
