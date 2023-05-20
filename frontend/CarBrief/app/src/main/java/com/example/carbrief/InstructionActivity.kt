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


class InstructionActivity : AppCompatActivity(), InstructionAdapter.ItemClickListener {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var instructionList: ArrayList<Instruction>
    private lateinit var imageIds: ArrayList<Int>
    private lateinit var headings: ArrayList<String>
    private var labelsSet = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarInstructions) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, BottomNavigationActivity::class.java)
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

        headings = arrayListOf()
        imageIds = arrayListOf()

        val detectedLabels = arrayListOf<String>()
        intent.getFloatArrayExtra("score")?.forEachIndexed { index, fl ->
            if (fl >= 0.45) {
                val label = classes?.get(index)?.let { labels?.get(it.toInt()) }

                if (label != null && !labelsSet.contains(label)) {
                    detectedLabels.add(label)
                    labelsSet.add(label)
                }
            }
        }
        detectedLabels.sort()
        for (label in detectedLabels) {
            topicsOnLabelsMapping[label]?.let { headings.add(getString(it)) }
            picturesOnLabelsMapping[label]?.let { imageIds.add(it) }
        }
        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)

        instructionList = arrayListOf()
        showInstructionsList()
    }

    private fun showInstructionsList() {

        for(i in imageIds.indices) {
            val instruction = Instruction(imageIds[i], headings[i])
            instructionList.add(instruction)
        }

        newRecyclerView.adapter = InstructionAdapter(instructionList, this@InstructionActivity)
    }

    override fun onItemClick(position: Int, tvHeading: String) {
        val intent = Intent(this, InstructionEntry::class.java)
        intent.putExtra("title", tvHeading)
        startActivity(intent)
    }
}