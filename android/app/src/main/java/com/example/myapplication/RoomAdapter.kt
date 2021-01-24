package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RoomAdapter.RoomViewHolder

class RoomAdapter(val context: Context, val roomList: ArrayList<Map<RoomModel, ArrayList<String>>>): RecyclerView.Adapter<RoomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.row_room, null)
        return RoomViewHolder(v)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val adapter = TagAdapter(roomList.get(position).values)
        holder.recyclerViewTag.setHasFixedSize(true)
        holder.recyclerViewTag.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerViewTag.adapter = adapter

        holder.tvRoomName.text = roomList[position].keys.toMutableList()[position].title
        holder.profile.setImageResource(R.drawable.add)
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profile = itemView.findViewById<ImageView>(R.id.imgRoomProfile)
        val tvRoomName = itemView.findViewById<TextView>(R.id.tvRoomName)
        val recyclerViewTag = itemView.findViewById<RecyclerView>(R.id.recyclerViewTag)
    }

}