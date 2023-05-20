package com.example.carbrief

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.ContextCompat
import com.example.carbrief.databinding.ActivityInstructionEntryBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class InstructionEntry : AppCompatActivity() {

    private lateinit var binding: ActivityInstructionEntryBinding
    private lateinit var favoritesSet: HashSet<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var favoritesSetString: String
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title")
        sharedPref = getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        favoritesSetString = getString(R.string.preference_favorites_set)
        favoritesSet = sharedPref.getStringSet(
            favoritesSetString, HashSet<String>()
        ) as HashSet<String>
        binding = ActivityInstructionEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title

        fab = findViewById(R.id.fab)
        if (favoritesSet.contains(title)) {
            fab.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.auxiliary_900)
            )
        }
        val climateControlLayout = findViewById<View>(R.id.climateControlLayout)
        val displayLayout = findViewById<View>(R.id.displayLayout)
        val steeringButtonsLayout = findViewById<View>(R.id.steeringButtonsLayout)
        val gearLeverLayout = findViewById<View>(R.id.gearLeverLayout)
        val lightControllerLayout = findViewById<View>(R.id.lightControllerLayout)
        val airBlowerLayout = findViewById<View>(R.id.airBlowerLayout)
        val musicSystemLayout = findViewById<View>(R.id.musicSystemLayout)
        val otherLayout = findViewById<View>(R.id.otherLayout)

        when (title) {
            getString(R.string.climate_control_topic) -> climateControlLayout.visibility = View.VISIBLE
            getString(R.string.display_topic) -> displayLayout.visibility = View.VISIBLE
            getString(R.string.steering_buttons_topic) -> steeringButtonsLayout.visibility = View.VISIBLE
            getString(R.string.gear_lever_topic) -> gearLeverLayout.visibility = View.VISIBLE
            getString(R.string.light_controller_topic) -> lightControllerLayout.visibility = View.VISIBLE
            getString(R.string.air_blower_topic) -> airBlowerLayout.visibility = View.VISIBLE
            getString(R.string.music_system_topic) -> musicSystemLayout.visibility = View.VISIBLE

            else -> {
                otherLayout.visibility = View.VISIBLE
            }
        }


        binding.fab.setOnClickListener {
            if (title != null) {
                if (!favoritesSet.contains(title)) {
                    fab.imageTintList= ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.auxiliary_900)
                    )
                    favoritesSet.add(title)
                    saveFavoritesList(favoritesSet)
                } else{
                    fab.imageTintList= ColorStateList.valueOf(ContextCompat.getColor(this, R.color.analogous_900))
                    favoritesSet.remove(title)
                    saveFavoritesList(favoritesSet)
                }
            }
        }
    }
    private fun saveFavoritesList(favoritesSet: HashSet<String>) {
        with (sharedPref.edit()) {
            putStringSet(favoritesSetString, favoritesSet)
            clear()
            commit()
        }
    }
}