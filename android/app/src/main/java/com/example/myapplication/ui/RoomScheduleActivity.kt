package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.*
import com.example.myapplication.R
import com.example.myapplication.ScheduleModel
import com.example.myapplication.dialog.ScheduleDialog
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_room_schedule.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RoomScheduleActivity: AppCompatActivity() {

    val TAG = "RoomSchedule"

    internal lateinit var preferences: SharedPreferences

    var sDate = ""
    var sYear= ""
    var sMonth=""
    var sDay=""

    var room_id = 0

    lateinit var event: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_schedule)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        if (intent.hasExtra("roomId")) {
            room_id = intent.getIntExtra("roomId", 0)
        }

        // String 타입 날짜를 long 타입으로 바꾸기
        var dateStr = "2021-01-03"
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var date = sdf.parse(dateStr)
        var longDate = date.time

        tvDateR.text = DateFormat.format("dd.EE", Date(System.currentTimeMillis()))
        tvTopDateR.text = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis()))
        event = calendarViewR.getEvents(Date(System.currentTimeMillis()))

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        calendarViewR.setFirstDayOfWeek(Calendar.SUNDAY)

        // 스케쥴 받아와서 점 찍기
        readSchedule()

        listRecyclerView()

        calendarViewR.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                event = calendarViewR.getEvents(dateClicked)

                sYear = SimpleDateFormat("yyyy").format(dateClicked)
                sMonth = SimpleDateFormat("MM").format(dateClicked)
                sDay= SimpleDateFormat("dd").format(dateClicked)

                tvDateR.text = "${sDay}.${DateFormat.format("EE", dateClicked)}"

                if (event.size != 0) {
                    // dateClicked = 클릭된 날짜
                    // events[position].data = 클릭된 날짜에 position번째 data보기
                    // Log.e(CAL_TAG, "DAY was clicked: ${dateClicked} with events ${events}")

                    for (i: Int in 0..event.size - 1) {
                        //Log.e(CAL_TAG, "events-data: ${events[i].data}")
                    }
                } else {
                    showToast("적용된 이벤트가 없습니다.")
                }

                listRecyclerView()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                tvTopDateR.text = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth)
            }

        })

        fabScheduleWriteR.setOnClickListener {
            val dialog = ScheduleDialog()
            dialog.sYear = sYear.toInt()
            dialog.sMonth = sMonth.toInt()
            dialog.sDay = sDay.toInt()

            dialog.listener = {date, content ->
                val ev = Event(Color.LTGRAY, sdf.parse("$sYear-$sMonth-$sDay").time, ScheduleModel(content, date))
                calendarViewR.addEvent(ev)
                Log.e("TAG", "등록했음: ${date}, ${content}")
            }

            dialog.show(supportFragmentManager, "dialog")
        }
    }

    fun listRecyclerView() {
        val mAdapter = MyAdapter()
        recyclerScheduleR.adapter = mAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerScheduleR.layoutManager = layoutManager
        recyclerScheduleR.setHasFixedSize(true)
    }

    fun readSchedule () {
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
                                val ev = Event(Color.MAGENTA, sdf.parse(result[i - 1].date).time, ScheduleWDTO(content, date, 1))
                                calendarViewR.addEvent(ev)
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

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@RoomScheduleActivity).inflate(R.layout.row_room_schedule, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val schedule: ScheduleWDTO = event[position].data as ScheduleWDTO
            Log.e(TAG, "${schedule.content}, ${schedule.date}")
            holder.tvUserId.text = schedule.user_id.toString()
            holder.tvContent.text = schedule.content
            holder.tvDate.text = schedule.date
        }

        override fun getItemCount(): Int {
            return event.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvUserId = itemView.findViewById<TextView>(R.id.tvUserId)
        val tvContent = itemView.findViewById<TextView>(R.id.tvContent)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDateS)
    }

    override fun onResume() {
        super.onResume()
        listRecyclerView()
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}