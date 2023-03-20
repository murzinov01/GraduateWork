package com.example.carbrief

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import kotlinx.coroutines.runBlocking

class SettingsActivity : AppCompatActivity() {
    private lateinit var propertyRepo: PropertyRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        propertyRepo = PropertyRepo(this)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarSettings) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavigationActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Btn LogIn
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            runBlocking {
                propertyRepo.saveProperty("refreshToken", "")
                propertyRepo.saveProperty("accessToken", "")
                propertyRepo.saveProperty("tokenType", "")
            }
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}