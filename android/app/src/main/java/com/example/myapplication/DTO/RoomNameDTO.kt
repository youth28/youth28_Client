package com.example.myapplication.DTO

data class RoomNameDTO(
        val room_name: String
) {
    override fun toString(): String {
        return "RoomNameDTO(room_name='$room_name')"
    }
}
