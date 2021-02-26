package com.example.myapplication.api

import com.example.myapplication.dto.schedule.ScheduleWDTO
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.room.RoomScheduleRDTO
import com.example.myapplication.dto.schedule.ScheduleRDTO
import com.example.myapplication.dto.id.UserId
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