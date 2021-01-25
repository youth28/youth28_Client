package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(val context: Context, val list: ArrayList<RoomModel>): RecyclerView.Adapter<RoomAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapter.Holder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.row_room, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: RoomAdapter.Holder, position: Int) {
        holder.imgProfile.setImageResource(R.drawable.add)
        holder.title.text = list[position].title

        val mAdapter = TagAdapter(context, list[position].field)
        holder.recyclerView.adapter = mAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.setHasFixedSize(true)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile = itemView.findViewById<ImageView>(R.id.imgRoomProfile)
        val title = itemView.findViewById<TextView>(R.id.tvRoomName)
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerViewTag)
    }

}