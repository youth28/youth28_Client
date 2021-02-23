package com.example.myapplication.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ChatModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomInfoDTO
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

    val profile = MutableLiveData<String>()
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
        profile.value = "img"
        UserData.userId = "dkstnqls"
        UserData.userNum = "6"

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
        /*
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
        */

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
}