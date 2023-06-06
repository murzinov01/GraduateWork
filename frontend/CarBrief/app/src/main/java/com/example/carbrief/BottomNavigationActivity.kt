package com.example.carbrief

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.carbrief.databinding.ActivityBottomNavigationBinding
import com.example.carbrief.interfaces.UsersService
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class BottomNavigationActivity : AppCompatActivity(), CoroutineScope {
    private var job: Job = Job()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var propertyRepo: PropertyRepo

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var binding : ActivityBottomNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        propertyRepo = PropertyRepo(this)
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

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
                R.id.navigation_settings -> replaceFragment(Settings())

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


        val usersApi = UsersService.create()
        var accessToken: String?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
        }

        runBlocking {
            accessToken = propertyRepo.readProperty("accessToken")
        }
        println("ACCESS TOKEN: $accessToken")
        val userNameTextFieldStored = sharedPref.getString("userNameTextField", "")

        if (accessToken != null && userNameTextFieldStored == "") {
            launch {
                val result = usersApi.getProfile(accessToken!!)
                if (result != null) {
                    with (sharedPref.edit()) {
                        putString("userNameTextField", result.username.toString())
                        println("userNameTextField")
                        // println(result.username.toString())
                        putString("emailTextField", result.email.toString())
                        clear()
                        commit()
                    }
                }
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}