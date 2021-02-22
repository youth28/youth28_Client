package com.example.myapplication.api

import com.example.myapplication.dto.UserId
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ImageAPI {

    @Multipart
    @POST("/image_upload")
    fun postImage(
            @Part("user_id") userId: UserId,
            @Part file: MultipartBody.Part,
            @Part("upload") name: RequestBody
    ): Call<ResponseBody>

    @Multipart
    @POST("/image_re_upload")
    fun modifyImage(
            @Part("user_id") userId: UserId,
            @Part file: MultipartBody.Part,
            @Part("upload") name: RequestBody
    ): Call<ResponseBody>
    
    @POST("/image_load")
    fun imageLoad(
            @Body userId: UserId
    ): Call<ResponseBody>
}