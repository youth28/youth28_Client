package com.example.myapplication.dto

data class RoomScheduleWDTO(
        val schedule_id: String,
        val content: String,
        val date: String,
        val user_id: Int
) {
    override fun toString(): String {
        return "RoomScheduleWDTO(schedule_id='$schedule_id', content='$content', date='$date', user_id=$user_id)"
    }
}
