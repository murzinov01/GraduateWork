package com.example.carbrief

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.carbrief.databinding.ActivityBottomNavigationBinding


class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding : ActivityBottomNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        binding.bottomNavigationView.selectedItemId = R.id.navigation_home
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> replaceFragment(Home())
                R.id.navigation_profile -> replaceFragment(Profile())
                R.id.navigation_favorite -> replaceFragment(Favorite())
                R.id.navigation_settings -> openSettings()

                else -> {

                }
            }
            true
        }
        binding.cameraBtn.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
            //replaceFragment(Camera())
        }

        replaceFragment(Home())
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun openSettings(){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
    }
}