package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ChatModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomInfoDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TalkViewModel: ViewModel() {
    val TAG = "TalkViewModel"

    val profile = MutableLiveData<String>()
    var title = MutableLiveData<String>()
    val tagList = MutableLiveData<ArrayList<String>>()
    val chatList = MutableLiveData<ArrayList<ChatModel>>()

    val onPlusEvent = SingleLiveEvent<Unit>()
    val onCalendarEvent = SingleLiveEvent<Unit>()

    var maxPro = 0
    var field = ""
    var room_id = 0

    init {
        profile.value = "img"

        val room = RoomId(room_id)

        val call = RetrofitHelper.getRoomApi().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        Log.e(TAG, response.body().toString())
                        field = response.body()!!.field
                        title.value = response.body()!!.title
                        profile.value = response.body()!!.profile
                        maxPro = response.body()!!.maxPeo

                        // region checkBox 설정하기
                        field = field.substring(0, field.length -1 )
                        Log.e(TAG+ "field", field)

                        val arrField = field.split(",")
                        val data = arrayListOf<String>()
                        for (itme in arrField) {
                            data.add(itme)
                        }
                        tagList.postValue(data)

                    }
                }
            }

            override fun onFailure(call: Call<RoomInfoDTO>, t: Throwable) {
                Log.e(TAG+" Err", "통신안됨: ${t.message}")
            }

        })
    }

    fun onPlus() {
        onPlusEvent.call()
    }

    fun onSend() {
        Log.e(TAG, "눌림")
        val data = arrayListOf<ChatModel>()
        for (i: Int in 1..10){
            if (i % 2 == 0){
                data.add(ChatModel("안녕?!$i", "2021-02-0$i", "subinJoHa", "img"))
            } else {
                data.add(ChatModel("잘지내?!$i", "2021-020$i", UserData.userId, "img"))
            }
        }
        chatList.postValue(data)
    }

    fun onCalendar() {
        onCalendarEvent.call()
    }
}