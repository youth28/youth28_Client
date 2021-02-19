package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MyRoom
import com.example.myapplication.databinding.MyRoomListBinding
import com.example.myapplication.view.TalkActivity

class MyRoomListAdapter (val context: Context): RecyclerView.Adapter<MyRoomListAdapter.Holder>() {
    var list = listOf<MyRoom>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = MyRoomListBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list = list[position])

        holder.itemView.setOnClickListener {
            Log.e("room","roomName: ${list[position].roomName}, roomId: ${list[position].roomId}")
            val intent = Intent(context, TalkActivity::class.java)
            intent.putExtra("roomName", list[position].roomName)
            intent.putExtra("roomId", list[position].roomId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: MyRoomListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: MyRoom) {
            binding.myroom = list
        }
    }


}