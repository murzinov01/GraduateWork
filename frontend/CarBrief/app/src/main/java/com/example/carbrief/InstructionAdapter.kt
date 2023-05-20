package com.example.carbrief

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class InstructionAdapter(private val instructionList: ArrayList<Instruction>, val listener: ItemClickListener) : RecyclerView.Adapter<InstructionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return instructionList.size
    }

    interface ItemClickListener{
        fun onItemClick(position: Int, tvHeading: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = instructionList[position]
        holder.titleImage.setImageResource(currentItem.titleImage)
        holder.tvHeading.text = currentItem.heading
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleImage: ShapeableImageView = itemView.findViewById(R.id.title_image)
        val tvHeading: TextView = itemView.findViewById(R.id.tvHeading)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                listener.onItemClick(position, tvHeading.text as String)
            }
        }
    }
}