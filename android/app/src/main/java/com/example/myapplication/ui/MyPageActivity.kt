package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.ScheduleModel
import com.example.myapplication.UserData
import com.example.myapplication.adapter.ScheduleAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityMyPageBinding
import com.example.myapplication.dialog.ScheduleDialog
import com.example.myapplication.viewmodel.MyPageViewModel
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.dialog_vote.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyPageActivity : AppCompatActivity() {

    val TAG = "MyPageActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityMyPageBinding
    private lateinit var viewModel: MyPageViewModel

    private val tagList = MutableLiveData<ArrayList<String>>()
    val event = MutableLiveData<List<Event>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userNum = preferences.getString("userNum", "5").toString()
        UserData.userName = preferences.getString("userName", "이것은 이름이다").toString()
        UserData.userId = preferences.getString("userId", "this is id").toString()
        UserData.userProfile = preferences.getString("userProfile", "imgg").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MyPageViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        with(viewModel){
            onModifyEvent.observe(this@MyPageActivity, {
                val intent = Intent(this@MyPageActivity, ModifyActivity::class.java)
                startActivity(intent)
            })
            onWriteScheduleEvent.observe(this@MyPageActivity, {
                // String 타입 날짜를 long 타입으로 바꾸기
                val sdf = SimpleDateFormat("yyyy-MM-dd")

                val dialog = ScheduleDialog()
                dialog.sYear = sYear.toInt()
                dialog.sMonth = sMonth.toInt()
                dialog.sDay = sDay.toInt()

                dialog.listener = {date, content ->
                    val ev = Event(Color.LTGRAY, sdf.parse("$sYear-$sMonth-$sDay").time, ScheduleModel(content, date))
                    binding.calendarView.addEvent(ev)
                    rcv()
                    Log.e("TAG", "등록했음: ${date}, ${content}")
                }

                dialog.show(supportFragmentManager, "dialog")
            })
            onReadScheduleEvent.observe(this@MyPageActivity, {
                binding.calendarView.addEvent(ev)
                Log.e("Activity", ev.toString())
            })
        }

        val dataObserver: Observer<ArrayList<String>>  =
                Observer { livedata ->
                    tagList.value = livedata
                    val mAdapter = TagAdapter(this)
                    binding.rcvTag.adapter = mAdapter
                    val layoutManager = LinearLayoutManager(this)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    binding.rcvTag.layoutManager = layoutManager
                    binding.rcvTag.setHasFixedSize(true)
                    mAdapter.list = tagList.value!!
                }
        viewModel.tagList.observe(this, dataObserver)

        event.value = calendarView.getEvents(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                event.value = binding.calendarView.getEvents(dateClicked)

                viewModel.sYear = SimpleDateFormat("yyyy").format(dateClicked)
                viewModel.sMonth = SimpleDateFormat("MM").format(dateClicked)
                viewModel.sDay= SimpleDateFormat("dd").format(dateClicked)

                viewModel.date.value = "${viewModel.sDay}.${DateFormat.format("EE", dateClicked)}"

                if (event.value!!.isNotEmpty()) {
                    for (i: Int in 0..event.value!!.size - 1) {
                        Log.e(TAG, "events-data: ${event.value!![i].data}")
                    }
                    rcv()
                } else {
                    showToast("적용된 이벤트가 없습니다.")
                }
                binding.invalidateAll()
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                viewModel.topDate.value = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth).toString()
                binding.invalidateAll()
            }

        })
    }

    fun rcv(){
        val sAdapter = ScheduleAdapter(this)
        binding.rcvSchedule.adapter = sAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.rcvSchedule.layoutManager = layoutManager
        binding.rcvSchedule.setHasFixedSize(true)
        sAdapter.event = event.value!!
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}