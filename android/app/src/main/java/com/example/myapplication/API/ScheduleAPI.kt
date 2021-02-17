package com.example.myapplication.api

import com.example.myapplication.ScheduleWDTO
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomScheduleRDTO
import com.example.myapplication.dto.ScheduleRDTO
import com.example.myapplication.dto.UserId
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ScheduleAPI {
    @POST("schedule_write")
    fun schedule_write(
            @Body scheduleWDTO: ScheduleWDTO
    ): Call<ScheduleWDTO>

    @POST("schedule_read")
    fun schedule_read(
            @Body user_id: UserId
    ): Call<ScheduleRDTO>

    @POST("room_schedule_read")
    fun room_schedule_read(
            @Body room_id: RoomId
    ): Call<RoomScheduleRDTO>
}