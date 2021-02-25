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
import com.example.myapplication.ScheduleWDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogScheduleBinding
import com.example.myapplication.databinding.DialogVoteMenuBinding
import com.example.myapplication.event.SingleLiveEvent
import kotlinx.android.synthetic.main.dialog_schedule.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteMenuDialog(): DialogFragment() {
    // 다이얼로그에 버튼이 눌리면
    var listener: (Int) -> Unit = { mode -> }

    val TAG = "ScheduleD"

    internal lateinit var preferences: SharedPreferences
    private lateinit var binding: DialogVoteMenuBinding

    var mode = 0

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.vote_menu_width)
        val height = resources.getDimensionPixelSize(R.dimen.vote_menu_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vote_menu, container, false)
        binding.dialog = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    fun onSingleClick() {
        mode = 1
        listener.invoke(mode)
        dismiss()
    }

    fun onMultipleClick() {
        mode = 2
        listener.invoke(mode)
        dismiss()
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}
