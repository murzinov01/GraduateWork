package com.example.carbrief

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.gpu.CompatibilityList


class Settings : Fragment() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var modelPreferencesKey: String
    private lateinit var useGpuKey: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = view.context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        modelPreferencesKey = getString(R.string.preference_model)
        useGpuKey = getString(R.string.preference_use_gpu)
        val modelType = sharedPref.getString(modelPreferencesKey, "")

        val radioBtnLight = view.findViewById<RadioButton>(R.id.btnLight)
        val radioBtnAverage = view.findViewById<RadioButton>(R.id.btnAverage)
        val radioBtnHeavy = view.findViewById<RadioButton>(R.id.btnHeavy)
        val gpuToggle = view.findViewById<SwitchMaterial>(R.id.gpuToggle)

        when (modelType) {
            "", "model0" -> {
                radioBtnLight.isChecked = true
                radioBtnAverage.isChecked = false
                radioBtnHeavy.isChecked = false
            }
            "model2" -> {
                radioBtnLight.isChecked = false
                radioBtnAverage.isChecked = true
                radioBtnHeavy.isChecked = false
            }
            else -> {
                radioBtnLight.isChecked = false
                radioBtnAverage.isChecked = false
                radioBtnHeavy.isChecked = true
            }
        }

        radioBtnLight.setOnClickListener {
            saveModelTypeInPreferences("model0")
            radioBtnLight.isChecked = true
            radioBtnAverage.isChecked = false
            radioBtnHeavy.isChecked = false
        }
        radioBtnAverage.setOnClickListener {
            saveModelTypeInPreferences("model2")
            radioBtnLight.isChecked = false
            radioBtnAverage.isChecked = true
            radioBtnHeavy.isChecked = false
        }
        radioBtnHeavy.setOnClickListener {
            saveModelTypeInPreferences("model4")
            radioBtnLight.isChecked = false
            radioBtnAverage.isChecked = false
            radioBtnHeavy.isChecked = true
        }

        gpuToggle.setOnClickListener {
            val compatList = CompatibilityList()
            if(compatList.isDelegateSupportedOnThisDevice)  {
                saveUseGpuFlag(true)
                gpuToggle.isChecked = true
            } else {
                val toast = Toast.makeText(
                    view.context, "GPU acceleration is not supported on your device!",
                    Toast.LENGTH_LONG
                )
                toast.show()
                saveUseGpuFlag(false)
                gpuToggle.isChecked = false
            }
        }
    }

    private fun saveModelTypeInPreferences(modelType: String) {
        with (sharedPref.edit()) {
            putString(modelPreferencesKey, modelType)
            clear()
            commit()
        }
    }

    private fun saveUseGpuFlag(flag: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(useGpuKey, flag)
            clear()
            commit()
        }
    }
}