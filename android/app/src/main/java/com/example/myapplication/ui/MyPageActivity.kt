package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.ScheduleRDTO
import com.example.myapplication.DTO.UserId
import com.example.myapplication.DTO.UserInfoDTO
import com.example.myapplication.R
import com.example.myapplication.ScheduleModel
import com.example.myapplication.dialog.ScheduleDialog
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.dialog_vote.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyPageActivity : AppCompatActivity() {

    val TAG = "MyPageActivity"

    internal lateinit var preferences: SharedPreferences

    val tagList = mutableListOf<String>()

    var strField = ""

    var sYear= ""
    var sMonth=""
    var sDay=""

    lateinit var event: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        // String 타입 날짜를 long 타입으로 바꾸기
        var dateStr = "2021-01-03"
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var date = sdf.parse(dateStr)

        tvDateMP.text = DateFormat.format("dd.EE", Date(System.currentTimeMillis()))
        tvTopDate.text = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis()))
        event = calendarViewMP.getEvents(Date(System.currentTimeMillis()))

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        calendarViewMP.setFirstDayOfWeek(Calendar.SUNDAY)

        // 스케쥴 받아와서 점 찍기
        readSchedule()

        listRecyclerView()

        calendarViewMP.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                event = calendarViewMP.getEvents(dateClicked)

                sYear = SimpleDateFormat("yyyy").format(dateClicked)
                sMonth = SimpleDateFormat("MM").format(dateClicked)
                sDay= SimpleDateFormat("dd").format(dateClicked)

                tvDateMP.text = "${sDay}.${DateFormat.format("EE", dateClicked)}"

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
                tvTopDate.text = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth)
            }

        })

        getUserInfo()

        btnModify.setOnClickListener {
            val intent = Intent(this@MyPageActivity, ModifyActivity::class.java)
            intent.putExtra("field", strField)
            startActivity(intent)
        }

        fabScheduleWriteMP.setOnClickListener {
            val dialog = ScheduleDialog()
            dialog.sYear = sYear.toInt()
            dialog.sMonth = sMonth.toInt()
            dialog.sDay = sDay.toInt()

            dialog.listener = {date, content ->
                val ev = Event(Color.LTGRAY, sdf.parse("$sYear-$sMonth-$sDay").time, ScheduleModel(content, date))
                calendarViewMP.addEvent(ev)
                Log.e("TAG", "등록했음: ${date}, ${content}")
            }

            dialog.show(supportFragmentManager, "dialog")
        }

    }

    fun getUserInfo() {
        val user = UserId(preferences.getString("userNum", "0")!!.toInt())
        val call = RetrofitHelper.getApiService().user_info(user)
        call.enqueue(object : Callback<UserInfoDTO> {
            override fun onResponse(call: Call<UserInfoDTO>, response: Response<UserInfoDTO>) {
                if (response.isSuccessful) {
                    val field = response.body()!!.field
                    strField = field
                    val arrField = field.split(",")
                    for (itme in arrField) {
                        tagList.add("#${itme}")
                    }
                    // recyclerView setting
                    val layouManager = LinearLayoutManager(this@MyPageActivity)
                    layouManager.orientation = LinearLayoutManager.HORIZONTAL
                    recyclerViewTag.layoutManager = layouManager
                    recyclerViewTag.adapter = MyAdapter()

                } else Log.e(TAG+"Err", response.message())
            }

            override fun onFailure(call: Call<UserInfoDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨: ${t.message}")
            }

        })

        tvName.text = preferences.getString("userName", "이름 없음")
        tvEmail.text = preferences.getString("userId", "Id 없음")
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.row_tag, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val tag = tagList[position]
            tag?.let {
                holder.tvTagName.text = tag
            }
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTagName = itemView.findViewById<TextView>(R.id.tvTagName)
    }

    fun listRecyclerView() {
        val sAdapter = ScheduleAdapter()
        recyclerScheduleMP.adapter = sAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerScheduleMP.layoutManager = layoutManager
        recyclerScheduleMP.setHasFixedSize(true)
    }


    fun readSchedule () {
        val userId = UserId(preferences.getString("userNum", "0")!!.toInt())
        val call = RetrofitHelper.getApiService().schedule_read(userId)
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
                                val ev = Event(Color.LTGRAY, sdf.parse(result[i - 1].date).time, ScheduleModel(content, date))
                                calendarViewMP.addEvent(ev)
                            }
                            listRecyclerView()
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

    inner class ScheduleAdapter : RecyclerView.Adapter<ScheduleViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
            return ScheduleViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.row_schedule, parent, false))
        }

        override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
            val schedule: ScheduleModel = event[position].data as ScheduleModel
            Log.e(TAG, "${schedule.content}, ${schedule.date}")
            holder.tvContent.text = schedule.content
            holder.tvDate.text = schedule.date
        }

        override fun getItemCount(): Int {
            return event.size
        }

    }

    inner class ScheduleViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvContent = itemView.findViewById<TextView>(R.id.tvContent)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDateS)
    }

    override fun onResume() {
        super.onResume()
        listRecyclerView()
        getUserInfo()
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}