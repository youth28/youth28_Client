package com.example.myapplication

class RoomData {
    companion object{
        var roomId = 0
        var roomField = ""
        var maxPeo = 0
        var profile = false
        var title = ""

        fun toStringData(): String {
            return "RoomData{roomId='${roomId}', roomField='$roomField', maxPeo='$maxPeo', title='$title'}"
        }
    }
}