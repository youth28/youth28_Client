package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ScheduleModel
import com.example.myapplication.databinding.RowScheduleBinding
import com.github.sundeepk.compactcalendarview.domain.Event

class ScheduleAdapter(val context: Context) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    val TAG = "ScheduleAdapter"

    var event = listOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = RowScheduleBinding.inflate(LayoutInflater.from(context), parent, false)

        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.onBind(schedule = event[position].data as ScheduleModel)
    }

    override fun getItemCount(): Int {
        return event.size
    }

    inner class ScheduleViewHolder (val binding: RowScheduleBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(schedule: ScheduleModel) {
            binding.row = schedule
        }
    }

}