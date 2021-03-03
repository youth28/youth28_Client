package com.example.myapplication.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ChatModel
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ChatListDTO
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.room.RoomInfoDTO
import com.example.myapplication.event.SingleLiveEvent
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TalkViewModel: ViewModel() {
    val TAG = "TalkViewModel"

    var title = MutableLiveData<String>()
    var msg = MutableLiveData<String>()
    val tagList = MutableLiveData<ArrayList<String>>()
    val chatList = MutableLiveData<ArrayList<ChatModel>>()

    val onPlusEvent = SingleLiveEvent<Unit>()
    val onCalendarEvent = SingleLiveEvent<Unit>()
    val onSendEvent = SingleLiveEvent<Unit>()

    var maxPro = 0
    var field = ""
    var room_id = 0
    var chat = arrayListOf<ChatModel>()
    val jsonObject = JSONObject()

    init {
        room_id = RoomData.roomId
        loadMessage()

        val room = RoomId(room_id)
        val call = RetrofitHelper.getRoomApi().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        Log.e(TAG, response.body().toString())
                        field = response.body()!!.field
                        title.value = response.body()!!.title
                        maxPro = response.body()!!.maxPeo

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

    fun addItem(item: ChatModel) {//아이템 추가
        if (chat != null) {
            chat.add(item)
            chatList.postValue(chat)
        }
    }

    fun onPlus() {
        onPlusEvent.call()
    }

    fun onSend() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val getTime = sdf.format(date)

        val message = msg.value.toString().trim { it <= ' ' }
        Log.e(TAG, "message=${message}, msg=${msg.value}")
        if (TextUtils.isEmpty(message)) {
            return
        }
        msg.value = ""
        try {
            jsonObject.put("user_id", UserData.userNum)
            jsonObject.put("msg", message)
            jsonObject.put("profile_image", "example")
            jsonObject.put("date_time", getTime)
            jsonObject.put("room_id", room_id)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        onSendEvent.call()
    }

    fun onCalendar() {
        onCalendarEvent.call()
    }

    fun loadMessage() {
        var call: Call<ChatListDTO>? = RetrofitHelper.getRoomApi().chat_load(RoomId(room_id))
        call?.enqueue(object : Callback<ChatListDTO> {
            override fun onResponse(call: Call<ChatListDTO>, response: Response<ChatListDTO>) {
                if (response.isSuccessful) {
                    Log.e("성공입니당~", response.body().toString())
                    var result = response.body()
                    var name: String
                    var msg: String
                    var user_id: String
                    var date_time: String

                    Log.e("result", result.toString())
                    for (i in 0..result!!.data.size - 1) {
                        Log.e("TAG_userId", result.data[i].user_id)
                        name = result.data[i].userName
                        msg = result.data[i].message
                        date_time = result.data[i].time
                        user_id = result.data[i].user_id

                        val format = ChatModel(userName = name, message = msg, imageUrl = "profileImage", time = date_time, user_id = user_id)
                        addItem(format)
                    }
                } else {
                    Log.e("실패", "통신오류: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ChatListDTO>, t: Throwable) {
                Log.e("d실패", t.message.toString())
            }

        })
    }



}