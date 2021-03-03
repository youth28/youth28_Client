package com.example.myapplication.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.dto.schedule.ScheduleModel
import com.example.myapplication.UserData
import com.example.myapplication.adapter.ScheduleAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.ActivityMyPageBinding
import com.example.myapplication.dialog.ScheduleDialog
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.viewmodel.MyPageViewModel
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_my_page.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyPageActivity : AppCompatActivity() {

    val TAG = "MyPageActivity"

    private lateinit var binding: ActivityMyPageBinding
    private lateinit var viewModel: MyPageViewModel

    private val tagList = MutableLiveData<ArrayList<String>>()
    val event = MutableLiveData<List<Event>>()

    var sDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MyPageViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        imageLoad(binding.imgProfile)

        with(viewModel){
            onModifyEvent.observe(this@MyPageActivity, {
                val intent = Intent(this@MyPageActivity, ModifyActivity::class.java)
                startActivity(intent)
            })
            onWriteScheduleEvent.observe(this@MyPageActivity, {
                val dialog = ScheduleDialog()
                dialog.sYear = sYear.toInt()
                dialog.sMonth = sMonth.toInt()
                dialog.sDay = sDay.toInt()

                dialog.listener = {
                    binding.calendarView.removeAllEvents()
                    viewModel.readSchedule()
                    rcv()
                }
                dialog.show(supportFragmentManager, "dialog")
            })
        }

        viewModel.event.observe(this, { livedata ->
            event.value = livedata
            binding.calendarView.removeEvents(sDate)
            binding.calendarView.addEvents(event.value)
            event.value = binding.calendarView.getEvents(sDate)
            Log.e("event", "event.observe")
            rcv()
        })

        viewModel.tagList.observe(this, { livedata ->
            tagList.value = livedata
            val mAdapter = TagAdapter(this)
            binding.rcvTag.adapter = mAdapter
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            binding.rcvTag.layoutManager = layoutManager
            binding.rcvTag.setHasFixedSize(true)
            mAdapter.list = tagList.value!!
        })

        event.value = calendarView.getEvents(Date(System.currentTimeMillis()))

        // 시작 요일 바꾸기
        binding.calendarView.setFirstDayOfWeek(Calendar.SUNDAY)

        binding.calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                sDate = dateClicked!!
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
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                viewModel.topDate.value = DateFormat.format("yyyy년 MM월", firstDayOfNewMonth).toString()
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

    fun imageLoad(img: CircleImageView) {
        val call = RetrofitHelper.getImageApi().imageLoad(UserId(UserData.userNum.toInt()))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("AAA", "REQUEST SUCCESS ==> ")
                    val file = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(file)
                    img.setImageBitmap(bitmap)
                    UserData.userProfile = true
                } else {
                    Log.d("AAA", "통신오류=${response.message()}")
                    img.setImageResource(R.drawable.add)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
                img.setImageResource(R.drawable.add)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo()
        imageLoad(binding.imgProfile)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}