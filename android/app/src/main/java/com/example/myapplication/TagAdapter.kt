package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TagAdapter(val data: Collection<ArrayList<String>>): RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.TagViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_tag, null)
        return TagViewHolder(v)
    }

    override fun onBindViewHolder(holder: TagAdapter.TagViewHolder, position: Int) {
        val tag = data.toTypedArray().get(position).get(position)
        holder.tagTitle.text = tag
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagTitle = itemView.findViewById<TextView>(R.id.tvTagName)
    }
}