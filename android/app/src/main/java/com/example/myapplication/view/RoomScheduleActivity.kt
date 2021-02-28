package com.example.myapplication.view

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.dto.schedule.ScheduleWDTO
import com.example.myapplication.UserData
import com.example.myapplication.dto.*
import com.example.myapplication.adapter.RoomScheduleAdapter
import com.example.myapplication.databinding.ActivityRoomScheduleBinding
import com.example.myapplication.dialog.ScheduleDialog
import com.example.myapplication.viewmodel.RoomScheduleViewModel
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_room_schedule.calendarView
import java.text.SimpleDateFormat
import java.util.*

class RoomScheduleActivity: AppCompatActivity() {

    val TAG = "RoomSchedule"

    internal lateinit var preferences: SharedPreferences
    
    private lateinit var binding: ActivityRoomScheduleBinding
    private lateinit var viewmodel: RoomScheduleViewModel

    var room_id = 0
    var sDate = Date()

    val event =  MutableLiveData<List<Event>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        if (intent.hasExtra("roomId")) {
            RoomData.roomId = intent.getIntExtra("roomId", 0)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_schedule)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomScheduleViewModel::class.java)
        binding.viewmodel = viewmodel
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        event.value = calendarView.getEvents(Date(System.currentTimeMillis()))

        with(viewmodel) {
            onWriteScheduleEvent.observe(this@RoomScheduleActivity, {
                // String 타입 날짜를 long 타입으로 바꾸기
                val sdf = SimpleDateFormat("yyyy-MM-dd")

                val dialog = ScheduleDialog()
                dialog.sYear = sYear.toInt()
                dialog.sMonth = sMonth.toInt()
                dialog.sDay = sDay.toInt()

                dialog.listener = {
                    binding.calendarView.removeAllEvents()
                    viewmodel.readSchedule()
                    rcv()
                }

                dialog.show(supportFragmentManager, "dialog")
            })
        }

        viewmodel.event.observe(this, { livedata ->
            event.value = livedata
            binding.calendarView.removeEvents(sDate)
            binding.calendarView.addEvents(event.value)
            event.value = binding.calendarView.getEvents(sDate)
            Log.e("event", "event.observe")
            rcv()
        })

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                sDate = dateClicked!!
                event.value = binding.calendarView.getEvents(dateClicked)

                viewmodel.sYear = SimpleDateFormat("yyyy").format(dateClicked)
                viewmodel.sMonth = SimpleDateFormat("MM").format(dateClicked)
                viewmodel.sDay= SimpleDateFormat("dd").format(dateClicked)

                viewmodel.date.value = "${viewmodel.sDay}.${DateFormat.format("EE", dateClicked)}"

                if (event.value!!.isNotEmpty()) {
                    for (i: Int in 0..event.value!!.size - 1) {
                        Log.e(TAG, "events-data: ${event.value!![i].data}")
                    }
                } else {
                    showToast("적용된 이벤트가 없습니다.")
                }
                rcv()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                viewmodel.topDate.value = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth).toString()
            }

        })
    }

    fun rcv(){
        val sAdapter = RoomScheduleAdapter(this)
        binding.recyclerSchedule.adapter = sAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerSchedule.layoutManager = layoutManager
        binding.recyclerSchedule.setHasFixedSize(true)
        sAdapter.event = event.value!!
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}