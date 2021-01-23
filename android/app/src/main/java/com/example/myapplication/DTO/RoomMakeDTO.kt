package com.example.myapplication.DTO

data class RoomMakeDTO(
        val title : String,
        val maxPeo : Int,
        val field : String,
        val profile : String
) {
    override fun toString(): String {
        return "RoomMakeDTO(title='$title', maxPeo=$maxPeo, field='$field', profile='$profile')"
    }
}
