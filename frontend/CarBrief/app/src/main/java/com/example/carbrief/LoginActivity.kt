package com.example.carbrief

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.example.carbrief.interfaces.AuthService
import com.example.carbrief.model.UserLoginModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var propertyRepo: PropertyRepo

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        propertyRepo = PropertyRepo(this)
        val authApi = AuthService.create()

        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarLogin) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)

        // btnLogin
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val username: String = inputUsername.text.toString()
            val password: String = inputPassword.text.toString()

            if (checkCredentials(username, password))
            {
                println("Login complete")

                // Create Login request to Backend
                val result = runBlocking {
                    val result = authApi.login(
                        UserLoginModel(username, password),
                        inputPassword,
                    )

                    if (result != null) {
                        result.access_token?.let { it1 -> propertyRepo.saveProperty("accessToken", it1) }
                        result.refresh_token?.let { it1 -> propertyRepo.saveProperty("refreshToken", it1) }
                        result.token_type?.let { it1 -> propertyRepo.saveProperty("tokenType", it1) }
                    }
                    result
                }
                if (result != null) {
                    switchToBottomNavigation()
                }
            } else {
                println("Login incomplete")
            }
        }
    }


    private fun switchToBottomNavigation() {
        val intent = Intent(this, BottomNavigationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkCredentials(username: String, password: String): Boolean {
        var isValid = true
        if (username.isEmpty() || username.length < minUserNameLength || username.length > maxUserNameLength) {
            showError(inputUsername, "Username isn't valid")
            isValid = false
        }
        if (password.isEmpty() || password.length < minPasswordLength) {
            showError(inputPassword, "Password must be at least 7 characters")
            isValid = false
        }
        return isValid
    }

    private fun showError(input: EditText, errorMessage: String) {
        input.error = errorMessage
        input.requestFocus()
    }
}