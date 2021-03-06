package com.example.myapplication.dto.user

data class UserModify(
        val user_id : Int,
        val name: String,
        val profile: String,
        val field: String
) {
    override fun toString(): String {
        return "UserModify(user_id=$user_id, name='$name', profile='$profile', field='$field')"
    }
}
