package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.ScheduleRDTO
import com.example.myapplication.DTO.UserId
import com.example.myapplication.R
import com.example.myapplication.RoomAdapter
import com.example.myapplication.ScheduleModel
import com.example.myapplication.dialog.ScheduleDialog
import kotlinx.android.synthetic.main.activity_calendar.*
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalendarActivity: AppCompatActivity() {

    val TAG = "CaledarA"

    internal lateinit var preferences: SharedPreferences

    var sDate = ""
    var sYear=0
    var sMonth=0
    var sDay=0

    val list : ArrayList<ScheduleModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        calendarView2.setOnDateChangeListener { view, year, month, dayOfMonth ->
            sYear = year
            sMonth = month+1
            sDay = dayOfMonth

            sDate = "${sMonth}월 ${sDay}일"
            tvDate.text = sDate

            list.clear()

            val userId = UserId(preferences.getString("userNum", "0")!!.toInt())
            val call = RetrofitHelper.getApiService().schedule_read(userId)
            call.enqueue(object : Callback<ScheduleRDTO> {
                override fun onResponse(call: Call<ScheduleRDTO>, response: Response<ScheduleRDTO>) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val result = response.body()!!.schedule
                                for (i: Int in 1..result.size) {
                                    list.add(ScheduleModel(result[i-1].content, result[i-1].date))
                                }
                                listRecyclerView()
                            }
                            204 -> {
                                Log.e(TAG, "저장된 스케줄이 없습니다.")
                            }
                        }
                    } else Log.e(TAG+"ERR" , "schedule_read, ERR: ${response.message()}")
                }

                override fun onFailure(call: Call<ScheduleRDTO>, t: Throwable) {
                    Log.e(TAG+"ERR", "통신안됨: ${t.message}")
                }

            })
        }

        fabScheduleWrite.setOnClickListener {
            val dialog = ScheduleDialog()

            dialog.sYear = sYear
            dialog.sMonth = sMonth
            dialog.sDay = sDay
            dialog.listener = {chekRB -> }
            dialog.show(supportFragmentManager, "dialog")
        }
    }

    fun listRecyclerView() {
        val mAdapter = MyAdapter()
        recyclerSchedule.adapter = mAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerSchedule.layoutManager = layoutManager
        recyclerSchedule.setHasFixedSize(true)
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@CalendarActivity).inflate(R.layout.row_schedule, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val schedule = list[position]
            schedule?.let {
                holder.tvContent.text = schedule.content
                holder.tvDate.text = schedule.date
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvContent = itemView.findViewById<TextView>(R.id.tvContent)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
    }
}