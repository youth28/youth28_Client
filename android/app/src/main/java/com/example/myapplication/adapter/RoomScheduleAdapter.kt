package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.dto.schedule.ScheduleWDTO
import com.example.myapplication.databinding.RowRoomScheduleBinding
import com.github.sundeepk.compactcalendarview.domain.Event


class RoomScheduleAdapter(val context: Context) : RecyclerView.Adapter<RoomScheduleAdapter.RoomScheduleViewHolder>() {
    val TAG = "RoomScheduleAdapter"

    var event = listOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomScheduleAdapter.RoomScheduleViewHolder {
        val binding = RowRoomScheduleBinding.inflate(LayoutInflater.from(context), parent, false)

        return RoomScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomScheduleAdapter.RoomScheduleViewHolder, position: Int) {
        holder.onBind(schedule = event[position].data as ScheduleWDTO)
    }

    override fun getItemCount(): Int {
        return event.size
    }

    class RoomScheduleViewHolder(val binding: RowRoomScheduleBinding): RecyclerView.ViewHolder(binding.root){
        fun onBind(schedule: ScheduleWDTO) {
            binding.row = schedule
        }
    }

}