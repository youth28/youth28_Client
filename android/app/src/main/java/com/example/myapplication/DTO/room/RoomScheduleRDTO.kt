package com.example.myapplication.dto.room

data class RoomScheduleRDTO(
        val room_schedule: List<RoomScheduleWDTO>
) {
    override fun toString(): String {
        return "RoomScheduleRDTO(room_schedule=$room_schedule)"
    }
}
