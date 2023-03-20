package com.example.carbrief.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenModel(
    var access_token: String? = null,
    var refresh_token: String? = null,
    var token_type: String? = null,
)
