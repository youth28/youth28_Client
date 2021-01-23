package com.example.myapplication.API

import com.example.myapplication.DTO.ResponseLogin
import com.example.myapplication.DTO.RoomMakeDTO
import com.example.myapplication.DTO.UserDTO
import com.example.myapplication.DTO.UserModify
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {
    @POST("register")
    fun register(
            @Body userDTO: UserDTO
    ): Call<UserDTO>

    @POST("login")
    fun login(
            @Body userDTO: UserDTO
    ): Call<ResponseLogin>

    @POST("register/check_email")
    fun check_emial(
            @Body userDTO: UserDTO
    ): Call<UserDTO>

    @POST("modify")
    fun modify(
            @Body userModify: UserModify
    ): Call<UserModify>

    @POST("make_room")
    fun make_room(
            @Body roomMakeDTO: RoomMakeDTO
    ): Call<RoomMakeDTO>
}