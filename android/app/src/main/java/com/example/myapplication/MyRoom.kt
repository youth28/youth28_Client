package com.example.myapplication

class MyRoom(
        val roomName: String,
        val roomId: Int
) {
    override fun toString(): String {
        return "MyRoom(roomName='$roomName', roomId=$roomId)"
    }
}