package com.example.myapplication.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogVoteBinding
import kotlinx.android.synthetic.main.dialog_vote.*

class VoteDialog : DialogFragment(){
    private lateinit var binding: DialogVoteBinding

    // 다이얼로그의 버튼이 눌린경우
    var listener: (String) -> Unit = {checkRB -> }

    var title =""
    var date = ""
    var writer = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_vote, container, false)
        binding.dialog = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.vote_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.vote_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun onVote(view: View) {
        var checkRB = ""
        when(radioGroup.checkedRadioButtonId){
            R.id.rbAgree -> {
                checkRB = "동의"
                dismiss()
            }
            R.id.rbOpposi -> {
                checkRB = "반대"
                dismiss()
            }
            R.id.rbHold -> {
                checkRB = "보류"
                dismiss()
            }
        }

        if(checkRB == ""){
            showToast("투표를 선택해 주세요")
        } else {
            listener.invoke(checkRB)
            dismiss()
        }
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}