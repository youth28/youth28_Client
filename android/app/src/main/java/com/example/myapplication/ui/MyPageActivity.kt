package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ScheduleRDTO
import com.example.myapplication.dto.UserId
import com.example.myapplication.dto.UserInfoDTO
import com.example.myapplication.R
import com.example.myapplication.ScheduleModel
import com.example.myapplication.adapter.ScheduleAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityMyPageBinding
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

    private lateinit var binding: ActivityMyPageBinding

    val tagList = mutableListOf<String>()

    var strField = ""
    var profile = ""
    var name = ""
    var email = ""
    var topDate = ""
    var date = ""

    var sYear= ""
    var sMonth=""
    var sDay=""

    lateinit var event: List<Event>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        binding.activity = this@MyPageActivity

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        date = DateFormat.format("dd.EE", Date(System.currentTimeMillis())).toString()
        topDate = DateFormat.format("yyyy년 MM월", Date(System.currentTimeMillis())).toString()
        event = calendarView.getEvents(Date(System.currentTimeMillis()))

        sYear = SimpleDateFormat("yyyy").format(Date(System.currentTimeMillis()))
        sMonth = SimpleDateFormat("MM").format(Date(System.currentTimeMillis()))
        sDay= SimpleDateFormat("dd").format(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        // 스케쥴 받아와서 점 찍기
        readSchedule()

        rcvSchedule()

        getUserInfo()

        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                event = binding.calendarView.getEvents(dateClicked)

                sYear = SimpleDateFormat("yyyy").format(dateClicked)
                sMonth = SimpleDateFormat("MM").format(dateClicked)
                sDay= SimpleDateFormat("dd").format(dateClicked)

                date = "${sDay}.${DateFormat.format("EE", dateClicked)}"

                if (event.size != 0) {
                    // dateClicked = 클릭된 날짜
                    // events[position].data = 클릭된 날짜에 position번째 data보기
                    // Log.e(CAL_TAG, "DAY was clicked: ${dateClicked} with events ${events}")

                    for (i: Int in 0..event.size - 1) {
                        Log.e(TAG, "events-data: ${event[i].data}")
                    }
                } else {
                    showToast("적용된 이벤트가 없습니다.")
                }

                rcvSchedule()
                binding.invalidateAll()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                topDate = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth).toString()
                binding.invalidateAll()
            }

        })
    }

    fun onModify(view: View) {
        val intent = Intent(this@MyPageActivity, ModifyActivity::class.java)
        intent.putExtra("field", strField)
        startActivity(intent)
    }

    fun onWriteSchedule(view: View) {
        // String 타입 날짜를 long 타입으로 바꾸기
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val dialog = ScheduleDialog()
        dialog.sYear = sYear.toInt()
        dialog.sMonth = sMonth.toInt()
        dialog.sDay = sDay.toInt()

        dialog.listener = {date, content ->
            val ev = Event(Color.LTGRAY, sdf.parse("$sYear-$sMonth-$sDay").time, ScheduleModel(content, date))
            binding.calendarView.addEvent(ev)
            Log.e("TAG", "등록했음: ${date}, ${content}")
        }

        dialog.show(supportFragmentManager, "dialog")
    }

    fun getUserInfo() {
        val user = UserId(preferences.getString("userNum", "0")!!.toInt())
        val call = RetrofitHelper.getUserApi().user_info(user)
        call.enqueue(object : Callback<UserInfoDTO> {
            override fun onResponse(call: Call<UserInfoDTO>, response: Response<UserInfoDTO>) {
                if (response.isSuccessful) {
                    val field = response.body()!!.field
                    strField = field
                    val arrField = field.split(",")
                    for (itme in arrField) {
                        tagList.add(itme)
                    }
                    // recyclerView setting
                    val layouManager = LinearLayoutManager(this@MyPageActivity)
                    layouManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.rcvTag.layoutManager = layouManager
                    binding.rcvTag.adapter = TagAdapter(this@MyPageActivity)

                } else Log.e(TAG+"Err", response.message())
            }

            override fun onFailure(call: Call<UserInfoDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨: ${t.message}")
            }

        })

        tagList.clear()
        for(i: Int in 1..5) {
            tagList.add("태그$i")
        }
        // recyclerView setting
        val mAdapter = TagAdapter(this)
        binding.rcvTag.adapter = mAdapter
        mAdapter.list = tagList
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rcvTag.layoutManager = layoutManager
        binding.rcvTag.setHasFixedSize(true)

        name = preferences.getString("userName", "이름 없음")!!
        email = preferences.getString("userId", "Id 없음")!!
    }

    fun rcvSchedule() {
        val sAdapter = ScheduleAdapter(this)
        binding.rcvSchedule.adapter = sAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rcvSchedule.layoutManager = layoutManager
        binding.rcvSchedule.setHasFixedSize(true)
        sAdapter.event = event
    }


    fun readSchedule () {
        val userId = UserId(preferences.getString("userNum", "0")!!.toInt())
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
                                val ev = Event(Color.LTGRAY, sdf.parse(result[i - 1].date).time, ScheduleModel(content, date))
                                binding.calendarView.addEvent(ev)
                            }
                            rcvSchedule()
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

    override fun onResume() {
        super.onResume()
        rcvSchedule()
        getUserInfo()
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}