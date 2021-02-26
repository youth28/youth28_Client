package com.example.myapplication.dto.room

class JoinRoomDTO(
        val user_id: Int,
        val room_id: Int
) {
    override fun toString(): String {
        return "JoinRoomDTO(user_id=$user_id, room_id=$room_id)"
    }
}