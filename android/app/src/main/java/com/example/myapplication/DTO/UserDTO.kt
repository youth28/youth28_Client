package com.example.myapplication.dto

import com.google.gson.annotations.SerializedName

class UserDTO {
    @SerializedName("email")
    private var email = ""
    @SerializedName("pwd")
    private var pwd = ""
    @SerializedName("name")
    private var name = ""
    @SerializedName("profile")
    private var profile = ""
    @SerializedName("field")
    private var field = ""

    // 회원가입
    constructor(email: String, pwd: String, name: String, field: String) {
        this.email = email
        this.pwd = pwd
        this.name = name
        this.field = field
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
        return "UserDTO(email='$email', pwd='$pwd', name='$name', profile='$profile', field='$field')"
    }

}