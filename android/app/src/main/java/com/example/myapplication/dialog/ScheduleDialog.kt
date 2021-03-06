package com.example.myapplication.dialog

import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.schedule.ScheduleWDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogScheduleBinding
import kotlinx.android.synthetic.main.dialog_schedule.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleDialog(): DialogFragment() {
    // 다이얼로그에 버튼이 눌리면
    var listener: () -> Unit = { }

    val TAG = "ScheduleD"

    internal lateinit var preferences: SharedPreferences
    private lateinit var binding: DialogScheduleBinding

    var sYear = 0
    var sMonth = 0
    var sDay = 0

    var sHour = 0
    var sMinute = 0

    var date = ""
    var time = ""
    var content = ""

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.schedule_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.schedule_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_schedule, container, false)
        binding.dialog = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        date = "${sYear}년 ${sMonth}월 ${sDay}일"

        time = "${timePicker.hour}-${timePicker.minute}"
        sHour = timePicker.hour
        sMinute = timePicker.minute
        
        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            time = "${hourOfDay}-${minute}"
            sHour = hourOfDay
            sMinute = minute
        }
    }

    fun onSave() {
        if (!content.isNotEmpty()) {
            showToast("내용을 입력하세요")
        } else {
            date = "${sYear}-${sMonth}-${sDay}-${time}"

            listener.invoke()

            preferences = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE)
            val writer = ScheduleWDTO(content, date, preferences.getString("userNum", "0")!!.toInt())
            val call = RetrofitHelper.getScheduleApi().schedule_write(writer)
            call.enqueue(object : Callback<ScheduleWDTO> {
                override fun onResponse(call: Call<ScheduleWDTO>, response: Response<ScheduleWDTO>) {
                    if (response.isSuccessful) {
                        Log.e("성공", response.message())
                        listener.invoke()
                    } else {
                        Log.e("실패", response.message())
                    }
                }

                override fun onFailure(call: Call<ScheduleWDTO>, t: Throwable) {
                    Log.e(TAG + "ERR", "통신안됨 ${t.message}")
                }

            })
            dismiss()
        }
    }

    fun onExit() {
        dismiss()
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}
