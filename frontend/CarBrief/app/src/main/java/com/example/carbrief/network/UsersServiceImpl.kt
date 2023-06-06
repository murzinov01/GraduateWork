package com.example.carbrief.network

import android.widget.EditText
import android.widget.Toast
import com.example.carbrief.interfaces.UsersService
import com.example.carbrief.model.DetailModel
import com.example.carbrief.model.ErrorModel
import com.example.carbrief.model.UserCreateModel
import com.example.carbrief.model.UserProfileModel
import com.example.carbrief.routes.ApiRoutes
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class UsersServiceImpl(
    private val client: HttpClient
) : UsersService {

    override suspend fun createUsers(userCreate: UserCreateModel, inputEmail: EditText, inputPassword: EditText): UserProfileModel? {
        return try {
            client.post<UserProfileModel> {
                url(ApiRoutes.USERS_CREATE)
                body = userCreate
            }
        } catch (ex: RedirectResponseException) {
            // 3xx - responses
            println("Error: ${ex.response.status.description}")
            null
        } catch (ex: ClientRequestException) {
            // 4xx - responses
            println("Error: ${ex.response.status.description}")
            val res = ex.response.readText(Charsets.UTF_8)
            val error_body = Json { ignoreUnknownKeys = true }.decodeFromString(ErrorModel.serializer(), res)
            println(error_body)
            for (errorDetail: DetailModel in error_body.detail) {
                for (attr: String in errorDetail.loc) {
                    if (attr == "password") {
                        inputPassword.error = "Password must be stronger"
                        inputPassword.requestFocus()
                    } else if (attr == "email") {
                        inputEmail.error = "Email isn't valid"
                        inputEmail.requestFocus()
                    }
                }
            }
            null
        } catch (ex: ServerResponseException) {
            // 5xx - response
            println("Error: ${ex.response.status.description}")
            val text = "Internal server error occurred"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(inputEmail.context, text, duration)
            toast.show()
            null
        }
    }

    override suspend fun getProfile(accessToken: String): UserProfileModel? {
        return try {
            client.get<UserProfileModel> {
                url(ApiRoutes.PROFILE)
                header("Authorization", "Bearer $accessToken")
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