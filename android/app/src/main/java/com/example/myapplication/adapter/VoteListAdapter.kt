package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.dto.VoteDTO
import com.example.myapplication.databinding.RowVoteBinding

class VoteListAdapter (val context: Context): RecyclerView.Adapter<VoteListAdapter.Holder>() {
    var list = listOf<VoteDTO>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowVoteBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])

        holder.itemView.setOnClickListener {
            Log.e("click", list[position].title)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: RowVoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: VoteDTO) {
            binding.vote = list
        }
    }
}