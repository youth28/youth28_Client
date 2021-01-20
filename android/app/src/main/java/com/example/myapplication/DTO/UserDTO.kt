package com.example.myapplication.DTO

import com.google.gson.annotations.SerializedName

public class UserDTO {
    @SerializedName("email")
    private var email = ""
    @SerializedName("pwd")
    private var pwd = ""
    @SerializedName("name")
    private var name = ""
    @SerializedName("user_img")
    private var img = ""

    // 회원가입
    constructor(email: String, pwd: String, name: String) {
        this.email = email
        this.pwd = pwd
        this.name = name
    }

    // 로그인
    constructor(email: String, pwd: String) {
        this.email = email
        this.pwd = pwd
    }

    // 중복 확인
    constructor(email: String) {
        this.email = email
    }

    override fun toString(): String {
        return "UserDTO(email='$email', pwd='$pwd', name='$name', img='$img')"
    }

}