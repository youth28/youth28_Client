package com.example.myapplication

class UserData {
    companion object{
        var userId = ""
        var userpassword = ""
        var userNum = ""
        var userName = ""
        var userProfile = ""

        fun toStringData(): String {
            return "UserData{userId='${userId}', userpassword='${userpassword}', userNum='${userNum}', userName='${userName}', userProfile='${userProfile}'}"
        }
    }
}