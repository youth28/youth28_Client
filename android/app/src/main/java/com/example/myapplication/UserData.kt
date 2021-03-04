package com.example.myapplication

class UserData {
    companion object{
        var userId = ""
        var userPassword = ""
        var userNum = ""
        var userName = ""
        var userProfile = false
        var userField = ""

        fun toStringData(): String {
            return "UserData{userId='${userId}', userpassword='${userPassword}', userNum='${userNum}', userName='${userName}', userProfile='${userProfile}', userField='${userField}'}"
        }
    }
}