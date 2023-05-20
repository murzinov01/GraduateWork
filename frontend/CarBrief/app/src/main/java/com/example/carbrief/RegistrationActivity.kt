package com.example.carbrief

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.carbrief.interfaces.UsersService
import com.example.carbrief.model.UserCreateModel
import com.example.carbrief.model.UserProfileModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext



class RegistrationActivity : AppCompatActivity() , CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputConfirmPassword: EditText


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarReg) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, StartScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        // btnHaveAccount
        val btnHaveAccount = findViewById<Button>(R.id.btnHaveAccount)
        btnHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        inputUsername = findViewById(R.id.inputUsername)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)

        // btnRegister
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val username: String = inputUsername.text.toString()
            val email: String = inputEmail.text.toString()
            val password: String = inputPassword.text.toString()
            val confirmPassword: String = inputConfirmPassword.text.toString()

            if (checkCredentials(username, email, password, confirmPassword))
            {
                println("Registration complete")
                // Create Register request to Backend

                val usersApi = UsersService.create()

                launch {
                    val result = usersApi.createUsers(
                        UserCreateModel(username, password, email),
                        inputEmail,
                        inputPassword,
                    )
                    println(result)
                    switchToLogin(result)
                }

            } else {
                println("Registration incomplete")
            }
        }
    }
    private fun switchToLogin(result: UserProfileModel?) {
        if (result != null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else{
            println("ERROR WITH REG")
        }
    }

    private fun checkCredentials(
        username: String, email: String, password: String, confirmPassword: String
    ): Boolean {
        var isValid = true
        if (username.isEmpty() || username.length < minUserNameLength || username.length > maxUserNameLength) {
            showError(inputUsername, "Username isn't valid")
            isValid = false
        }
        if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmail, "Email isn't valid")
            isValid = false
        }
        if (password.isEmpty() || password.length < minPasswordLength) {
            showError(inputPassword, "Password must be at least 7 characters")
            isValid = false
        }
        if (confirmPassword.isEmpty() || confirmPassword != password) {
            showError(inputConfirmPassword, "Passwords don't match")
            isValid = false
        }
        return isValid
    }

    private fun showError(input: EditText, errorMessage: String) {
        input.error = errorMessage
        input.requestFocus()
    }
}