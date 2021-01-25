package com.example.myapplication.API

import com.example.myapplication.DTO.*
import com.example.myapplication.MyRoom
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

    @POST("modify_user")
    fun modify(
            @Body userModify: UserModify
    ): Call<UserModify>

    @POST("make_room")
    fun make_room(
            @Body roomMakeDTO: RoomMakeDTO
    ): Call<RoomMakeDTO>

    @POST("modify_room")
    fun modify_room(
            @Body roomModifyDTO: RoomModifyDTO
    ): Call<RoomModifyDTO>

    @POST("room_info")
    fun room_info(
            @Body room_id: Int
    ): Call<RoomMakeDTO>

    @POST("room_search")
    fun room_search(
            @Body field: String
    ): Call<RoomsDTO>

    @POST("my_room")
    fun my_room(
            @Body user_id: Int
    ): Call<MyRoomsDTO>
}