package com.example.myapplication.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.JoinRoomDTO
import com.example.myapplication.DTO.ScheduleWDTO
import com.example.myapplication.DTO.UserId
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_join.view.*
import kotlinx.android.synthetic.main.dialog_join.view.btnNo
import kotlinx.android.synthetic.main.dialog_join.view.btnYes
import kotlinx.android.synthetic.main.dialog_join.view.tvScheduleDialog
import kotlinx.android.synthetic.main.dialog_schedule.view.*
import kotlinx.android.synthetic.main.dialog_vote.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleDialog(): DialogFragment() {
    var mDate : String? = null
    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mDate = requireArguments().getString(ARG_DIALOG_MAIN_MSG)
        }
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.dialog_w)
        val height = resources.getDimensionPixelSize(R.dimen.dialog_h)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val view: View = requireActivity().layoutInflater.inflate(R.layout.dialog_schedule, null)
        view.tvScheduleDialog.text = mDate
        val content: String = view.editContent.text.toString()
        var date = "${sYear}-${sMonth}-${sDay}-"
        view.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            date+= "${hourOfDay}-${minute}"
        }

        view.btnYes.setOnClickListener {
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
                    Log.e(TAG+"ERR", "통신안됨 ${t.message}")
                }

            })
            dismiss()
        }
        view.btnNo.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_schedule, container, false)
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "CustomDialogFragment"
        const val ARG_DIALOG_MAIN_MSG = "dialog_main_msg"
        var sYear = 0
        var sMonth = 0
        var sDay = 0

        fun newInstance(year: Int, month: Int, day: Int): ScheduleDialog {
            val bundle = Bundle()
            sYear = year
            sMonth = month +1
            sDay = day
            val dateMsg = "${sYear}년 ${sMonth}월 ${sDay}일"
            bundle.putString(ARG_DIALOG_MAIN_MSG, dateMsg)
            val fragment = ScheduleDialog()
            fragment.arguments = bundle
            return fragment
        }
    }
}
