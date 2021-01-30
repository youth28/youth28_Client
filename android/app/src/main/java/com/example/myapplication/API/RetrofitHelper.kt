package com.example.myapplication.API

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    const val API_URL = "http://b8f98d34e53d.ngrok.io/"

    private var instanc: Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getApiService(): UserAPI {
        return getInstance().create(UserAPI::class.java)
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