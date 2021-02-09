package com.example.myapplication.dto

data class ResponseLogin(
        val user_id: String,
        val name: String,
        val profile: String
){


    override fun toString(): String {
        return "ResponseLogin(name='$name', profile='$profile', userId=$user_id)"
    }


}