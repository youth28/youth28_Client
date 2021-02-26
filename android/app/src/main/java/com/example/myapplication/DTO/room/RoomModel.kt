package com.example.myapplication.dto.room

data class RoomModel(
        val room_id : Int,
        val title: String,
        val maxPeo: Int,
        val field: List<String>,
        val profile: String
) {
    override fun toString(): String {
        return "RoomModel(room_id=$room_id, title='$title', maxPeo=$maxPeo, field='$field', profile='$profile')"
    }
}
