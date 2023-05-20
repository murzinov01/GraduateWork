package com.example.carbrief.interfaces

import android.widget.EditText
import com.example.carbrief.model.TokenModel
import com.example.carbrief.model.UserLoginModel
import com.example.carbrief.network.AuthServiceImpl
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

interface AuthService {
    suspend fun login(userLogin: UserLoginModel, inputPassword: EditText): TokenModel?
    suspend fun refresh(refreshToken: String): TokenModel?

    companion object {
        fun create(): AuthService {
            return AuthServiceImpl(
                client = HttpClient(Android) {
                    // Logging
                    install(Logging) {
                        level = LogLevel.ALL
                    }

                    install(JsonFeature) {
                        serializer = KotlinxSerializer(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
                        acceptContentTypes = acceptContentTypes + ContentType.Any
                    }

                    // Timeout
                    install(HttpTimeout) {
                        requestTimeoutMillis = 15000L
                        connectTimeoutMillis = 15000L
                        socketTimeoutMillis = 15000L
                    }
                    // Apply to all requests
//                    defaultRequest {
//                        // Parameter("api_key", "some_api_key")
//                        // Content Type
//                        if (method != HttpMethod.Get) contentType(ContentType.Application.Json)
//                        accept(ContentType.Application.Json)
//                    }
                }
            )
        }

//        private val json = kotlinx.serialization.json.Json {
//            ignoreUnknownKeys = true
//            isLenient = true
//            encodeDefaults = false
//        }
    }
}