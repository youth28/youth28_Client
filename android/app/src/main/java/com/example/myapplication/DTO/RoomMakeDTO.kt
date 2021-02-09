package com.example.myapplication.dto

data class RoomMakeDTO(
        val title : String,
        val maxPeo : Int,
        val field : String,
        val profile : String,
        val user_id: Int
) {
    override fun toString(): String {
        return "RoomMakeDTO(title='$title', maxPeo=$maxPeo, field='$field', profile='$profile', user_id='$user_id')"
    }
}
