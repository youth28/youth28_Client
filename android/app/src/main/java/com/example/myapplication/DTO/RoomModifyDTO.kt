package com.example.myapplication.DTO

data class RoomModifyDTO(
        val room_id : Int,
        val title : String,
        val maxPeo : Int,
        val field : String,
        val profile : String
) {
    override fun toString(): String {
        return "RoomModifyDTO(room_id=$room_id, title='$title', maxPeo=$maxPeo, field='$field', profile='$profile')"
    }
}
