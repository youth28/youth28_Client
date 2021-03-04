package com.example.myapplication.dialog.vote

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
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogPostVoteBinding

class PostVoteDialog : DialogFragment(){
    private lateinit var binding: DialogPostVoteBinding

    // 다이얼로그의 버튼이 눌린경우
    var listener: (String) -> Unit = {date -> }

    var title = MutableLiveData<String>()
    var date = MutableLiveData<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_post_vote, container, false)
        binding.dialog = this

        binding.apply {
            date.value = "${datePicker.year}-${datePicker.month}-${datePicker.dayOfMonth}"
            Log.e("day", "${datePicker.year}-${datePicker.month}-${datePicker.dayOfMonth}")
        }
        binding.datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            date.value = "${year}-${monthOfYear}-${dayOfMonth}"
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.post_vote_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.post_vote_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    fun onCreateVote() {
        listener.invoke(date.value.toString())
        dismiss()
    }

}