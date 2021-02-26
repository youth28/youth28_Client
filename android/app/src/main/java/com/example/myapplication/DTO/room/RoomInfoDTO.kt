package com.example.myapplication.dto.room

data class RoomInfoDTO (
        val title : String,
        val maxPeo : Int,
        val field : String,
        val profile : String,
        val room_manager: Int
        ) {
    override fun toString(): String {
        return "RoomInfoDTO(title='$title', maxPeo=$maxPeo, field='$field', profile='$profile', room_manager=$room_manager)"
    }
}