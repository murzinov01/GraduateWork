package com.example.carbrief

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Favorite : Fragment(), InstructionAdapter.ItemClickListener {
    private lateinit var favoritesSet: HashSet<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var favoritesSetString: String
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var instructionList: ArrayList<Instruction>
    private lateinit var imageIds: ArrayList<Int>
    private lateinit var headings: ArrayList<String>
    private lateinit var curView: View
    private lateinit var noFavoritesLayout: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        curView = view
        noFavoritesLayout = view.findViewById(R.id.noFavorites)
        sharedPref = view.context.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        favoritesSetString = getString(R.string.preference_favorites_set)

        newRecyclerView = view.findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(view.context)
        newRecyclerView.setHasFixedSize(true)
    }

    private fun showInstructionsList() {
        instructionList = arrayListOf()
        headings = arrayListOf()
        imageIds = arrayListOf()

        favoritesSet = sharedPref.getStringSet(
            favoritesSetString, HashSet<String>()
        ) as HashSet<String>
        for(title in favoritesSet) {
            headings.add(title)
            val imageKey = getKey(topicsOnLabelsMapping, title)
            picturesOnLabelsMapping[imageKey]?.let { imageIds.add(it) }
        }

        if (imageIds.size > 0) {
            for(i in imageIds.indices) {
                val instruction = Instruction(imageIds[i], headings[i])
                instructionList.add(instruction)
            }
        }
        else {
            noFavoritesLayout.visibility = View.VISIBLE
        }
        newRecyclerView.adapter = InstructionAdapter(instructionList, this@Favorite)
    }

    private fun getKey(map: Map<String, Int>, target: String): String? {
        for ((key, value) in map)
        {
            if (getString(value) == target) {
                return key
            }
        }
        return null
    }

    override fun onItemClick(position: Int, tvHeading: String) {
        val intent = Intent(curView.context, InstructionEntry::class.java)
        intent.putExtra("title", tvHeading)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        showInstructionsList()
    }
}