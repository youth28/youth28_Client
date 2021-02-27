package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.dto.room.RoomModel
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.room.RoomNameDTO
import com.example.myapplication.dto.room.RoomsDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomFindViewModel: ViewModel() {
    val TAG = "RoomFindViewModel"

    val key = MutableLiveData<String>()
    val errMsg = MutableLiveData<String>()

    val list = MutableLiveData<ArrayList<RoomModel>>()

    fun onFind() {
        if (key.value == "" || key.value == null) {
           errMsg.value = "방 제목을 입력해주세요."
        } else {
            val roomName = RoomNameDTO(key.value.toString())
            val call = RetrofitHelper.getRoomApi().room_search(roomName)
            call.enqueue(object : Callback<RoomsDTO> {
                override fun onResponse(call: Call<RoomsDTO>, response: Response<RoomsDTO>) {
                    if(response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val result = response.body()
                                val data = arrayListOf<RoomModel>()
                                for (i: Int in 1..result!!.room.size) {
                                    val info = result.room[i-1]
                                    val field = info.field.substring(0, result.room[i-1].field.length -1)
                                    val fieldArray = field.split(",")
                                    data.add(RoomModel(info.room_id, info.title, info.maxPeo,
                                            fieldArray, info.profile))
                                    Log.e(TAG, "RoomModel(room_id=${info.room_id}, title='${info.title}', maxPeo=${info.maxPeo}," +
                                            " field='$fieldArray', profile='${info.profile}')")
                                }
                                list.postValue(data)
                            }
                            204 -> {
                                errMsg.value = "해당 제목의 방이 존재하지 않습니다."
                            }
                        }
                    } else Log.e(TAG+"ERR", "RoomFind통신: ${response.message()}")
                }

                override fun onFailure(call: Call<RoomsDTO>, t: Throwable) {
                    Log.e(TAG+"ERR", "통신안됨: ${t.message}")
                }

            })
        }
    }
}