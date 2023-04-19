package com.example.carbrief

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InstructionActivity : AppCompatActivity() {

    private lateinit var newRecyclerView: RecyclerView
    private lateinit var instructionList: ArrayList<Instruction>
    lateinit var imageIds: Array<Int>
    lateinit var headings: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        val toolbar: Toolbar = findViewById<View>(R.id.toolbarInstructions) as Toolbar

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }


        imageIds = arrayOf(

        )
        headings = arrayOf(

        )
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