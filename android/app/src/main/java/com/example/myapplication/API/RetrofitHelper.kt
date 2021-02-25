package com.example.myapplication.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    const val API_URL = "http://ff069ad73b26.ngrok.iu/"
    private var instanc: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getUserApi(): UserAPI {
        return getInstance().create(UserAPI::class.java)
    }

    fun getRoomApi(): RoomAPI {
        return getInstance().create(RoomAPI::class.java)
    }

    fun getScheduleApi(): ScheduleAPI {
        return getInstance().create(ScheduleAPI::class.java)
    }

    fun getImageApi(): ImageAPI {
        return getInstance().create(ImageAPI::class.java)
    }

    fun getVoteApi(): VoteAPI {
        return getInstance().create(VoteAPI::class.java)
    }

    fun getInstance(): Retrofit {
        if (instanc == null) {
            instanc = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
        }

        return instanc!!
    }

}