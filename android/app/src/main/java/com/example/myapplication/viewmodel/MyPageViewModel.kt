package com.example.myapplication.viewmodel

import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.ScheduleModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ScheduleRDTO
import com.example.myapplication.dto.UserId
import com.example.myapplication.dto.UserInfoDTO
import com.example.myapplication.event.SingleLiveEvent
import com.github.sundeepk.compactcalendarview.domain.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyPageViewModel: ViewModel() {
    val TAG = "MyPageViewModel"

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val profile = MutableLiveData<String>()
    val topDate = MutableLiveData<String>()
    val date = MutableLiveData<String>()

    val tagList = MutableLiveData<ArrayList<String>>()
    val event = MutableLiveData<List<Event>>()

    val onModifyEvent = SingleLiveEvent<Unit>()
    val onWriteScheduleEvent = SingleLiveEvent<Unit>()
    val onReadScheduleEvent = SingleLiveEvent<Unit>()

    var sYear= ""
    var sMonth=""
    var sDay=""
    var strField = ""
    lateinit var ev : Event

    init {
        getUserInfo()
        readSchedule()

        date.value = DateFormat.format("dd.EE", Date(System.currentTimeMillis())).toString()
        topDate.value = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis())).toString()

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))
    }

    fun onModify() {
        strField = "안녕,자기,렛,미,쉣더,부리"
        UserData.userField = strField
        UserData.userNum = "5"
        onModifyEvent.call()
    }

    fun onWriteSchedule() {
        onWriteScheduleEvent.call()
    }

    fun getUserInfo() {
        profile.value = "img"
        name.value = UserData.userName
        email.value = UserData.userId

        val user = UserId(UserData.userNum.toInt())
        val call = RetrofitHelper.getUserApi().user_info(user)
        call.enqueue(object : Callback<UserInfoDTO> {
            override fun onResponse(call: Call<UserInfoDTO>, response: Response<UserInfoDTO>) {
                if (response.isSuccessful) {
                    val field = response.body()!!.field
                    strField = field
                    val arrField = field.split(",")
                    val data = arrayListOf<String>()
                    for (itme in arrField) {
                        data.add(itme)
                    }
                    tagList.postValue(data)
                } else Log.e(TAG+"Err", response.message())
            }

            override fun onFailure(call: Call<UserInfoDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨: ${t.message}")
            }

        })

        val data = arrayListOf<String>()
        for(i: Int in 1..5) {
            data.add("태그$i")
        }
        tagList.postValue(data)
    }

    fun readSchedule () {
        val userId = UserId(UserData.userNum.toInt())
        val call = RetrofitHelper.getScheduleApi().schedule_read(userId)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        call.enqueue(object : Callback<ScheduleRDTO> {
            override fun onResponse(call: Call<ScheduleRDTO>, response: Response<ScheduleRDTO>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            val result = response.body()!!.schedule
                            for (i: Int in 1..result.size) {
                                val content = result[i-1].content
                                val rDate = result[i-1].date
                                val arrDate = rDate.split("-")
                                val date = "${arrDate[3]}:${arrDate[4]}"
                                ev = Event(Color.LTGRAY, sdf.parse(result[i - 1].date).time, ScheduleModel(content, date))
                                Log.e("viewMode", ev.toString())
                                onReadScheduleEvent.call()
                            }
                        }
                        204 -> {
                            Log.e(TAG, "저장된 스케줄이 없습니다.")
                        }
                    }
                } else Log.e(TAG + "ERR", "schedule_read, ERR: ${response.message()}")
            }

            override fun onFailure(call: Call<ScheduleRDTO>, t: Throwable) {
                Log.e(TAG + "ERR", "통신안됨: ${t.message}")
            }

        })
    }

}