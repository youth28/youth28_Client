package com.example.myapplication.dialog

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.ScheduleWDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.dialog_schedule.*
import kotlinx.android.synthetic.main.dialog_schedule.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleDialog(): DialogFragment() {

    val TAG = "ScheduleD"

    internal lateinit var preferences: SharedPreferences

    var sYear = 0
    var sMonth = 0
    var sDay = 0

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.schedule_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.schedule_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvScheduleDialog.text = "${sYear}년 ${sMonth}월 ${sDay}일"

        val content: String = view.editContent.text.toString()

        var date = "${sYear}-${sMonth}-${sDay}-"
        timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            date+= "${hourOfDay}-${minute}"
        }

        view.btnYes.setOnClickListener {
            if (content == "") {
                showToast("내용을 입력하세요")
            } else {

                preferences = requireActivity().getSharedPreferences("user", Activity.MODE_PRIVATE)
                val writer = ScheduleWDTO(content, date, preferences.getString("userNum", "0")!!.toInt())
                val call = RetrofitHelper.getApiService().schedule_write(writer)
                call.enqueue(object : Callback<ScheduleWDTO> {
                    override fun onResponse(call: Call<ScheduleWDTO>, response: Response<ScheduleWDTO>) {
                        if (response.isSuccessful) {
                            Log.e("성공", response.message())
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
        view.btnNo.setOnClickListener {
            dismiss()
        }
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}
