package com.example.carbrief

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.FileInputStream


class InstructionActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var instructionList: ArrayList<Instruction>
    private lateinit var imageIds: ArrayList<Int>
    private lateinit var headings: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarInstructions) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }


        var bmp: Bitmap? = null
        val filename = intent.getStringExtra("image")
        try {
            val inputStream: FileInputStream = openFileInput(filename)
            bmp = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val location = intent.getFloatArrayExtra("location")
        val classes = intent.getFloatArrayExtra("classes")
        val labels = intent.getStringArrayExtra("labels")

        println("CLASSES NEW")
        println(classes)

        headings = arrayListOf()
        imageIds = arrayListOf()

        intent.getFloatArrayExtra("score")?.forEachIndexed { index, fl ->
            if (fl >= 0.45) {
                val label = classes?.get(index)?.let { labels?.get(it.toInt()) }
                headings.add(String.format("Instruction for %s", label))
                imageIds.add(R.drawable.test_image)
            }
        }
        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        instructionList = arrayListOf()
        getUserData()
    }

    private fun getUserData() {

        for(i in imageIds.indices) {
            val instruction = Instruction(imageIds[i], headings[i])
            instructionList.add(instruction)
        }

        newRecyclerView.adapter = InstructionAdapter(instructionList)

    }
}