package com.example.myapplication.dto.room

data class RoomDTO(
        val room_id: Int,
        val title: String,
        val maxPeo: Int,
        val field: String,
        val profile: String
) {
    override fun toString(): String {
        return "RoomDTO(room_id=$room_id, title='$title', maxPeo=$maxPeo, field='$field', profile='$profile')"
    }
}
