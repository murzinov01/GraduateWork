package com.example.carbrief.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorModel(
    var detail: List<DetailModel>
)

@Serializable
data class DetailModel(
    var loc: List<String>,
    var msg: String? = null,
    var type: String? = null,
)

@Serializable
data class ErrorLoginModel(
    var detail: String? = null,
)