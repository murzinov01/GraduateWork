package com.example.carbrief.network

import android.widget.EditText
import android.widget.Toast
import com.example.carbrief.interfaces.AuthService
import com.example.carbrief.model.*
import com.example.carbrief.routes.ApiRoutes
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class AuthServiceImpl(
    private val client: HttpClient
) : AuthService {

    override suspend fun login(userLogin: UserLoginModel, inputPassword: EditText): TokenModel? {
        return try {
            client.post<TokenModel> {
                url(ApiRoutes.LOGIN)
                body =
                    FormDataContent(
                        Parameters.build {
                            userLogin.username?.let { append("username", it) }
                            append("grant_type", "password")
                            userLogin.password?.let { append("password", it) }
                            append("client_secret", "")
                        }
                    )
            }
        } catch (ex: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ClientRequestException) {
            // 4xx - responses
            println("Error: ${ex.response.status.description}")
            val res = ex.response.readText(Charsets.UTF_8)
            val error_body = Json { ignoreUnknownKeys = true }.decodeFromString(ErrorLoginModel.serializer(), res)
            if (error_body.detail == "Incorrect username or password")
            {
                inputPassword.error = "Incorrect password"
                inputPassword.requestFocus()
            }
            null
        } catch (ex: ServerResponseException) {
            // 5xx - response
            println("Error: ${ex.response.status.description}")
            val text = "Internal server error occurred"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(inputPassword.context, text, duration)
            toast.show()
            null
        }
    }

    override suspend fun refresh(refreshToken: String): TokenModel? {
        return try {
            client.post<TokenModel> {
                url(ApiRoutes.REFRESH)
                parameter("refresh_token", refreshToken)
            }
        } catch (ex: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ClientRequestException) {
            // 4xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ServerResponseException) {
            // 5xx - response
            println("Error: ${ex.response.status.description}")
            null
        }
    }
}