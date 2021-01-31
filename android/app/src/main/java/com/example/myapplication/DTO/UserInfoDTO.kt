package com.example.myapplication.DTO

data class UserInfoDTO(
        val email: String,
        val pwd: String,
        val name: String,
        val profile: String,
        val field: String
) {
    override fun toString(): String {
        return "UserInfoDTO(email='$email', pwd='$pwd', name='$name', profile='$profile', field='$field')"
    }
}
