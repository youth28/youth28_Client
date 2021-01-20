package com.example.myapplication

class ChatModel (
        val message: String,
        val time: String,
        val userId: String,
        val imageUrl: String = ""
        ){
    override fun toString(): String {
        return "ChatModel(message='$message', time='$time', userId='$userId', imageUrl='$imageUrl')"
    }
}