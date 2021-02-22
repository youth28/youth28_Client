package com.example.myapplication.viewmodel

import android.graphics.Color
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.RoomData
import com.example.myapplication.ScheduleWDTO
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomScheduleRDTO
import com.example.myapplication.event.SingleLiveEvent
import com.github.sundeepk.compactcalendarview.domain.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomScheduleViewModel: ViewModel() {
    val TAG = "RoomScheduleViewModel"

    val date = MutableLiveData<String>()
    val topDate = MutableLiveData<String>()
    val event = MutableLiveData<List<Event>>()

    val onWriteScheduleEvent = SingleLiveEvent<Unit>()
    val onReadScheduleEvent = SingleLiveEvent<Unit>()

    var sYear= ""
    var sMonth=""
    var sDay=""
    var room_id = 0
    lateinit var ev : Event

    init {
        room_id = RoomData.roomId
        readSchedule()

        date.value = DateFormat.format("dd.EE", Date(System.currentTimeMillis())).toString()
        topDate.value = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis())).toString()

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))
    }

    fun onWriteSchedule() {
        onWriteScheduleEvent.call()
    }

    fun readSchedule () {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val data = arrayListOf<Event>()
        for (i: Int in 1..10) {
            data.add(Event(Color.MAGENTA, sdf.parse("2021-2-23-10-$i").time, ScheduleWDTO("하이루 $i", "10:$i", UserData.userNum.toInt())))
        }
        Log.e(TAG, data.toString())
        event.postValue(data)


        val roomId = RoomId(room_id)
        Log.e(TAG, roomId.toString())
        val call = RetrofitHelper.getScheduleApi().room_schedule_read(roomId)
        call.enqueue(object : Callback<RoomScheduleRDTO> {
            override fun onResponse(call: Call<RoomScheduleRDTO>, response: Response<RoomScheduleRDTO>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            val data = arrayListOf<Event>()
                            val result = response.body()!!.room_schedule
                            for (i: Int in 1..result.size) {
                                val content = result[i-1].content
                                val rDate = result[i-1].date
                                val arrDate = rDate.split("-")
                                val date = "${arrDate[3]}:${arrDate[4]}"
                                val ev = Event(Color.MAGENTA, sdf.parse(result[i - 1].date).time,
                                        ScheduleWDTO(content, date, UserData.userNum.toInt()))
                                data.add(ev)
                                Log.e("viewMode", ev.toString())
                            }
                            event.postValue(data)
                            onReadScheduleEvent.call()
                        }
                        204 -> {
                            Log.e(TAG, "저장된 스케줄이 없습니다.")
                        }
                    }
                } else Log.e(TAG, "schedule_read, ERR: ${response.message()}")
            }

            override fun onFailure(call: Call<RoomScheduleRDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨: ${t.message}")
            }

        })
    }
}