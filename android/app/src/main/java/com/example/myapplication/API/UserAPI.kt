package com.example.myapplication.API

import com.example.myapplication.DTO.*
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
            @Body room_id: RoomId
    ): Call<RoomInfoDTO>

    @POST("room_search")
    fun room_search(
            @Body field: RoomNameDTO
    ): Call<RoomsDTO>

    @POST("my_room")
    fun my_room(
            @Body user_id: UserId
    ): Call<MyRoomsDTO>

    @POST("room_list")
    fun room_list(
            @Body user_id: UserId
    ): Call<MyRoomsDTO>

    @POST("room_join")
    fun room_join(
            @Body joinRoomDTO: JoinRoomDTO
    ): Call<JoinRoomDTO>

    @POST("schedule_write")
    fun schedule_write(
            @Body scheduleWDTO: ScheduleWDTO
    ): Call<ScheduleWDTO>

    @POST("schedule_read")
    fun schedule_read(
            @Body user_id: UserId
    ): Call<ScheduleRDTO>

    @POST("user_info")
    fun user_info(
            @Body user_id: UserId
    ): Call<UserInfoDTO>
}