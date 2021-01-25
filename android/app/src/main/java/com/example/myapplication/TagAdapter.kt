package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TagAdapter(val context: Context, val list: List<String>): RecyclerView.Adapter<TagAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_tag, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: TagAdapter.Holder, position: Int) {
        val tag = list[position]
        holder.tagName.text = "#${tag}"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        val tagName = itemView.findViewById<TextView>(R.id.tvTagName)
    }

}