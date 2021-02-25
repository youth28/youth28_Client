package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.MyRoom
import com.example.myapplication.RoomModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.MyRoomsDTO
import com.example.myapplication.dto.UserId
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {
    val TAG = "MainViewModel"

    val list = MutableLiveData<ArrayList<RoomModel>>()
    val myRoomList = MutableLiveData<ArrayList<MyRoom>>()

    val onMenuEvent = SingleLiveEvent<Unit>()
    val onSearchEvent = SingleLiveEvent<Unit>()
    val onRoomMakeEvent = SingleLiveEvent<Unit>()

    init {
        val data = arrayListOf<MyRoom>()
        for (i: Int in 1..5){
            data.add(MyRoom("이것은 방 이름${i}", i))
        }
        myRoomList.postValue(data)

        Log.e(TAG, "init 입니당")

        rcvRoomList()
        rcvMyRoomList()
    }

    fun onMenu() {
        onMenuEvent.call()
    }

    fun onSearch() {
        onSearchEvent.call()
    }

    fun onRoomMake() {
        onRoomMakeEvent.call()
    }

    fun rcvRoomList() {
        val user_id = UserId(UserData.userNum.toInt())
        val call = RetrofitHelper.getRoomApi().room_list(user_id)
        call.enqueue(object : Callback<MyRoomsDTO> {
            override fun onResponse(call: Call<MyRoomsDTO>, response: Response<MyRoomsDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    for (i: Int in 1..result!!.room.size) {
                        val field = result.room[i-1].field.substring(0, result.room[i-1].field.length -1 )
                        val fieldArray = field.split(",")
                        val info = result.room[i-1]

                        val data = ArrayList<RoomModel>()
                        data.add(RoomModel(info.room_id, info.title, info.maxPeo,
                                fieldArray, info.profile))
                        list.postValue(data)
                        Log.e(TAG, "RoomModel(room_id=${info.room_id}, title='${info.title}', maxPeo=${info.maxPeo}," +
                                " field='$fieldArray', profile='${info.profile}')")
                    }
                } else {
                    Log.e(TAG, "메인 리스트: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG, "통신 안됨: ${t.message}")
            }

        })
    }

    fun rcvMyRoomList() {
        val user_id = UserId(UserData.userNum.toInt())
        val call2 = RetrofitHelper.getUserApi().my_room(user_id = user_id)
        call2.enqueue(object : Callback<MyRoomsDTO> {
            override fun onResponse(call: Call<MyRoomsDTO>, response: Response<MyRoomsDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val data = ArrayList<MyRoom>()
                    myRoomList.postValue(data)
                    for (i: Int in 1..result!!.room.size) {
                        data.add(MyRoom(result.room[i - 1].title, result.room[i - 1].room_id))
                        Log.e(TAG, myRoomList.value!![i-1].toString())
                    }
                    myRoomList.postValue(data)

                } else {
                    Log.e(TAG, "사이드 리스트: ${response.message()}")
                }

            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG + "ERR", "통신 안됨: ${t.message}")
            }

        })
    }

}