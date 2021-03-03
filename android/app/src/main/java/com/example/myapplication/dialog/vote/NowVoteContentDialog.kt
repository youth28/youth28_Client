package com.example.myapplication.dialog.vote

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
import com.example.myapplication.databinding.DialogNowVoteContentBinding
import com.example.myapplication.dto.id.QuestionId
import com.example.myapplication.dto.vote.VoteStatusDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NowVoteContentDialog : DialogFragment(){
    private lateinit var binding: DialogNowVoteContentBinding

    val title = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val writer = MutableLiveData<String>()
    val deadLine = MutableLiveData<String>()
    val contentList = arrayListOf(
            MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>()
    )
    val cntList = arrayListOf(
            MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>(),MutableLiveData<String>()
    )

    var questionId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_now_vote_content, container, false)
        binding.dialog = this

        onNowVote()

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


    fun onNowVote() {
        val call = RetrofitHelper.getVoteApi().now_vote(QuestionId(questionId))
        call.enqueue(object : Callback<VoteStatusDTO> {
            override fun onResponse(call: Call<VoteStatusDTO>, response: Response<VoteStatusDTO>) {
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
                        if (isNotNull(contentList[i].value))
                            contentList[i].value = "없는 항목입니다."
                    }

                    cntList[0].value = "${result.content1_cnt}표"
                    cntList[1].value = "${result.content2_cnt}표"
                    cntList[2].value = "${result.content3_cnt}표"
                    cntList[3].value = "${result.content4_cnt}표"
                    cntList[4].value = "${result.content5_cnt}표"

                    binding.invalidateAll()

                } else Log.e("now_vote", " 통신오류: ${response.message()}")
            }

            override fun onFailure(call: Call<VoteStatusDTO>, t: Throwable) {
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