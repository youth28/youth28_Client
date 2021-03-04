package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RowTagBinding

class TagAdapter(val context: Context): RecyclerView.Adapter<TagAdapter.Holder>() {
    var list = listOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowTagBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: RowTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: String) {
            binding.tag = "#${list}"
        }
    }

}