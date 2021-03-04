package com.example.myapplication.api

import com.example.myapplication.dto.*
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.dto.room.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RoomAPI {
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

    @POST("room_list")
    fun room_list(
            @Body user_id: UserId
    ): Call<MyRoomsDTO>

    @POST("room_join")
    fun room_join(
            @Body joinRoomDTO: JoinRoomDTO
    ): Call<JoinRoomDTO>

    @POST("chat_load")
    fun chat_load(
            @Body room_id: RoomId
    ): Call<ChatListDTO>
}