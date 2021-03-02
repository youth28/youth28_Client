package com.example.myapplication

class RoomData {
    companion object{
        var roomId = 0
        var roomField = ""
        var maxPeo = 0
        var profile = ""
        var title = ""

        fun toStringData(): String {
            return "UserData{roomId='${roomId}', roomField='$roomField', maxPeo='$maxPeo', title='$title'}"
        }
    }
}