package com.example.myapplication.dto.room

data class RoomModifyDTO(
        val room_id : Int,
        val title : String,
        val maxPeo : Int,
        val field : String
) {
    override fun toString(): String {
        return "RoomModifyDTO(room_id=$room_id, title='$title', maxPeo=$maxPeo, field='$field')"
    }
}
