package com.example.myapplication.dto

import com.example.myapplication.dto.room.RoomDTO

data class MyRoomsDTO (
        val count: Int,
        val room: List<RoomDTO>
        ) {
}