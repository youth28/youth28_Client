package com.example.myapplication.api

import com.example.myapplication.dto.*
import com.example.myapplication.ScheduleWDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {
    @POST("register")
    fun register(
            @Body userDTO: UserDTO
    ): Call<ResponseLogin>

    @POST("login")
    fun login(
            @Body userDTO: UserDTO
    ): Call<ResponseLogin>

    @POST("register/check_email")
    fun check_emial(
            @Body userDTO: UserDTO
    ): Call<UserDTO>

    @POST("user_info")
    fun user_info(
            @Body user_id: UserId
    ): Call<UserInfoDTO>

    @POST("modify_user")
    fun modify(
            @Body userModify: UserModify
    ): Call<UserModify>

    @POST("my_room")
    fun my_room(
            @Body user_id: UserId
    ): Call<MyRoomsDTO>
}