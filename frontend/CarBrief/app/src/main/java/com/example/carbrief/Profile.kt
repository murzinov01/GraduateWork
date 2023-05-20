package com.example.carbrief

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.runBlocking


class Profile : Fragment() {
    private lateinit var propertyRepo: PropertyRepo
    private lateinit var profileImage: ShapeableImageView
    private lateinit var profileImageButton: FloatingActionButton
    private lateinit var sharedPref: SharedPreferences
    private lateinit var profileImageUriKey: String
    private val profileImageName = "profile_image_name"
    private var profileImageBitMap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        propertyRepo = PropertyRepo(view.context)
        sharedPref = view.context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        profileImageUriKey = getString(R.string.profile_image_uri)
        val profileImageUri = sharedPref.getString(profileImageUriKey, "").toString()

        // Btn LogOut
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            runBlocking {
                propertyRepo.saveProperty("refreshToken", "")
                propertyRepo.saveProperty("accessToken", "")
                propertyRepo.saveProperty("tokenType", "")
            }
            val intent = Intent(view.context, LoginActivity::class.java)
            startActivity(intent)
        }

        profileImage = view.findViewById(R.id.ProfileImage)
        profileImageButton = view.findViewById(R.id.ProfileImageButton)

        if (profileImageUri == "") {
            val contentPadding = 20
            val scale = resources.displayMetrics.density
            val contentPaddingInPx = (contentPadding * scale + 0.5f).toInt()
            profileImage.setContentPadding(contentPaddingInPx, contentPaddingInPx, contentPaddingInPx, contentPaddingInPx)
        } else {
            profileImage.setImageURI(profileImageUri.toUri())
            profileImage.setContentPadding(0, 0, 0, 0)
        }

        profileImageButton.setOnClickListener {

            ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("RESULT CODE")
        println(resultCode)
        println("IMAGE URI")
        println(data?.data)
        val imageUri = data?.data
        profileImage.setImageURI(data?.data)
        profileImage.setContentPadding(0, 0, 0, 0)
        with (sharedPref.edit()) {
            putString(profileImageUriKey, imageUri.toString())
            clear()
            commit()
        }
    }
}