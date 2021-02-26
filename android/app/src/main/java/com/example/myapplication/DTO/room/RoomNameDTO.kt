package com.example.myapplication.dto.room

data class RoomNameDTO(
        val room_name: String
) {
    override fun toString(): String {
        return "RoomNameDTO(room_name='$room_name')"
    }
}
