package com.example.myapplication.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.R
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.DialogDoVoteBinding
import com.example.myapplication.databinding.DialogDoVoteContentBinding
import com.example.myapplication.dto.QuestionId
import com.example.myapplication.dto.VoteDetailDTO
import kotlinx.android.synthetic.main.dialog_do_vote.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoContentVoteDialog : DialogFragment(){
    private lateinit var binding: DialogDoVoteContentBinding

    // 다이얼로그의 버튼이 눌린경우
    var listener: (String) -> Unit = {checkRB -> }


    val title = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val writer = MutableLiveData<String>()
    val deadLine = MutableLiveData<String>()
    val seleContent = MutableLiveData<String>()
    val contentList = arrayListOf(
            MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>()
    )
    val checkList = arrayListOf(
            MutableLiveData(false), MutableLiveData(false), MutableLiveData(false), MutableLiveData(false), MutableLiveData(false)
    )
    val clickAbleList = arrayListOf(
            MutableLiveData(true), MutableLiveData(true), MutableLiveData(true), MutableLiveData(true), MutableLiveData(true)
    )

    var questionId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_do_vote_content, container, false)
        binding.dialog = this

        onLookVote()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.do_vote_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.do_vote_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun onVote() {
        for (i: Int in 0..4) {
            if (checkList[i].value == true)
                seleContent.value = contentList[i].value
        }

        Log.e("selectContent", seleContent.value.toString())

        
    }

    fun onLookVote() {
        title.value = "question_text"
        date.value = "투표 시작일: 2021-02-25"
        writer.value = "게시자: 이름임돠"
        deadLine.value = "투표 마감일: 2021-02-26"

        contentList[0].value = "선택지1"
        contentList[1].value = "선택지2"
        contentList[2].value = "null"
        contentList[3].value = ""
        contentList[4].value = null

        for (i: Int in 0..4) {
            if (isNotNull(contentList[i].value)) {
                clickAbleList[i].value = false
                contentList[i].value = "없는 항목입니다."
            }
        }

        val call = RetrofitHelper.getVoteApi().look_vote(QuestionId(questionId))
        call.enqueue(object : Callback<VoteDetailDTO> {
            override fun onResponse(call: Call<VoteDetailDTO>, response: Response<VoteDetailDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    title.value = result.question_text
                    date.value = "투표 시작일: ${result.time}"
                    writer.value = "게시자: ${result.name}"
                    deadLine.value = "투표 마감일: ${result.dead_line}"

                    contentList[0].value = result.content1
                    contentList[1].value = result.content2
                    contentList[2].value = result.content3
                    contentList[3].value = result.content4
                    contentList[4].value = result.content5

                    for (i: Int in 0..4) {
                        if (!isNotNull(contentList[i].value)) {
                            clickAbleList[i].value = true
                            contentList[i].value = "없는 항목입니다."
                        }
                    }

                } else Log.e("look_vote", " 통신오류: ${response.message()}")
            }

            override fun onFailure(call: Call<VoteDetailDTO>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })
    }

    fun isNotNull(value: String?): Boolean {
        return (value == null || value == "" || value == "null")
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}