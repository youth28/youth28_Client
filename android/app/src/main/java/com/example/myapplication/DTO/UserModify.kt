package com.example.myapplication.DTO

data class UserModify(
        val user_id : Int,
        val name: String,
        val profile: String
) {

    override fun toString(): String {
        return "UserModify(user_id=$user_id, name='$name', profile='$profile')"
    }
}
