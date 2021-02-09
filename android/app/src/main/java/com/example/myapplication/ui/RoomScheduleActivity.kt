package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.*
import com.example.myapplication.R
import com.example.myapplication.ScheduleWDTO
import com.example.myapplication.adapter.RoomScheduleAdapter
import com.example.myapplication.databinding.ActivityRoomScheduleBinding
import com.example.myapplication.dialog.ScheduleDialog
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_room_schedule.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomScheduleActivity: AppCompatActivity() {

    val TAG = "RoomSchedule"

    internal lateinit var preferences: SharedPreferences
    
    private lateinit var binding: ActivityRoomScheduleBinding
    
    var sYear= ""
    var sMonth=""
    var sDay=""
    
    var date = ""
    var topDate = ""

    var room_id = 0

    lateinit var event: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_schedule)
        binding.activity = this@RoomScheduleActivity

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        if (intent.hasExtra("roomId")) {
            room_id = intent.getIntExtra("roomId", 0)
        }

        date = DateFormat.format("dd.EE", Date(System.currentTimeMillis())).toString()
        topDate = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis())).toString()
        event = binding.calendarView.getEvents(Date(System.currentTimeMillis()))

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        // 스케쥴 받아와서 점 찍기
        readSchedule()

        listRecyclerView()

        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                event = binding.calendarView.getEvents(dateClicked)

                sYear = SimpleDateFormat("yyyy").format(dateClicked)
                sMonth = SimpleDateFormat("MM").format(dateClicked)
                sDay= SimpleDateFormat("dd").format(dateClicked)

                date = "${sDay}.${DateFormat.format("EE", dateClicked)}"

                if (event.size != 0) {
                    for (i: Int in 0..event.size - 1) {

                    }
                } else {
                    showToast("적용된 이벤트가 없습니다.")
                }
                listRecyclerView()
                binding.invalidateAll()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                topDate = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth).toString()
                binding.invalidateAll()
            }

        })
    }
    
    fun onWriteSchedule(view: View) {
        // String 타입 날짜를 long 타입으로 바꾸기
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val dialog = ScheduleDialog()
        dialog.sYear = sYear.toInt()
        dialog.sMonth = sMonth.toInt()
        dialog.sDay = sDay.toInt()

        dialog.listener = {date, content ->
            val ev = Event(Color.LTGRAY, sdf.parse("$sYear-$sMonth-$sDay").time,
                    ScheduleWDTO(content, date, preferences.getString("userNum", "0")!!.toInt() ))
            binding.calendarView.addEvent(ev)
            Log.e("TAG", "등록했음: ${date}, ${content}")
        }

        dialog.show(supportFragmentManager, "dialog")
    }

    fun readSchedule () {
        /*for (i: Int in 1..10) {
            val evev = Event(Color.MAGENTA, System.currentTimeMillis(),
                            ScheduleWDTO("메롱$i", "안유빈 바보", 5))
            binding.calendarView.addEvent(evev)
        }*/

        val roomId = RoomId(room_id)
        Log.e(TAG, roomId.toString())
        val call = RetrofitHelper.getApiService().room_schedule_read(roomId)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        call.enqueue(object : Callback<RoomScheduleRDTO> {
            override fun onResponse(call: Call<RoomScheduleRDTO>, response: Response<RoomScheduleRDTO>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> {
                            val result = response.body()!!.room_schedule
                            for (i: Int in 1..result.size) {
                                val content = result[i-1].content
                                val rDate = result[i-1].date
                                val arrDate = rDate.split("-")
                                val date = "${arrDate[3]}:${arrDate[4]}"
                                val ev = Event(Color.MAGENTA, sdf.parse(result[i - 1].date).time,
                                        ScheduleWDTO(content, date, preferences.getString("userNum", "0")!!.toInt()))
                                binding.calendarView.addEvent(ev)
                            }
                            listRecyclerView()
                        }
                        204 -> {
                            Log.e(TAG, "저장된 스케줄이 없습니다.")
                        }
                    }
                } else Log.e(TAG + "ERR", "schedule_read, ERR: ${response.message()}")
            }

            override fun onFailure(call: Call<RoomScheduleRDTO>, t: Throwable) {
                Log.e(TAG + "ERR", "통신안됨: ${t.message}")
            }

        })
    }

    fun listRecyclerView() {
        val mAdapter = RoomScheduleAdapter(this)
        binding.recyclerSchedule.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerSchedule.layoutManager = layoutManager
        binding.recyclerSchedule.setHasFixedSize(true)
        mAdapter.event = event
    }

    override fun onResume() {
        super.onResume()
        listRecyclerView()
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}