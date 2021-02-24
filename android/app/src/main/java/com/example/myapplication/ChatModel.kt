package com.example.myapplication

class ChatModel (
        val message: String,
        val time: String,
        val user_id: String,
        val userName: String,
        val imageUrl: String = ""
        ){
    override fun toString(): String {
        return "ChatModel(message='$message', time='$time', user_id='$user_id', imageUrl='$imageUrl', userName='$userName')"
    }
}