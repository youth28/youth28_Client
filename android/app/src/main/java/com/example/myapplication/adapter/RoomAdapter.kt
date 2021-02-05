package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RoomModel
import com.example.myapplication.databinding.RowRoomBinding
import com.example.myapplication.dialog.JoinDialog

class RoomAdapter(val context: Context): RecyclerView.Adapter<RoomAdapter.Holder>() {
    var list = listOf<RoomModel>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowRoomBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list = list[position])

        val mAdapter = TagAdapter(context)
        holder.binding.rcvTag.adapter = mAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.binding.rcvTag.layoutManager = layoutManager
        holder.binding.rcvTag.setHasFixedSize(true)
        mAdapter.list = list[position].field

        holder.itemView.setOnClickListener {
            val dialog = JoinDialog()
            dialog.room_id = list[position].room_id
            dialog.mMainMsg = "${list[position].title}"
            val manager = (context as AppCompatActivity).supportFragmentManager
            dialog.show(manager, "dialog")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: RowRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: RoomModel) {
            binding.room = list
        }
    }



}