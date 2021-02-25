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
import com.example.myapplication.databinding.DialogPostVoteContentBinding
import com.example.myapplication.dto.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostVoteContentDialog: DialogFragment() {
    private lateinit var binding: DialogPostVoteContentBinding

    // 다이얼로그의 버튼이 눌린경우
    var listener: (String) -> Unit = {date -> }

    var date = ""
    var title = MutableLiveData<String>()
    val content1 = MutableLiveData<String>()
    val content2 = MutableLiveData<String>()
    val content3 = MutableLiveData<String>()
    val content4 = MutableLiveData<String>()
    val content5 = MutableLiveData<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_post_vote_content, container, false)
        binding.dialog = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.do_vote_content_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.do_vote_content_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun onCreateVote() {

        /*
        val voteMakeDTO = VoteMakeDTO(title.value.toString(), content1.value.toString(), content2.value.toString(),
                content3.value.toString(), content4.value.toString(), content5.value.toString(), date.toString(), "2021-02-27", false, 5, 6)
        val call = RetrofitHelper.getVoteApi().question_make(voteMakeDTO = voteMakeDTO)
        call.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("response: ", response.code().toString())
                    listener.invoke(date)
                    dismiss()
                } else Log.e("response: ", "통신 안됨, ${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })
         */

/*
        val call2 = RetrofitHelper.getVoteApi().vote(DoVoteDTO("content1", 8, 2))
        call2.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("vote: ", response.code().toString())
                    listener.invoke(date)
                    dismiss()
                } else Log.e("vote: ", "통신 안됨, ${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })

 */

        /*
        val call3 = RetrofitHelper.getVoteApi().look_vote(QuestionId(2))
        call3.enqueue(object : Callback<VoteDetailDTO> {
            override fun onResponse(call: Call<VoteDetailDTO>, response: Response<VoteDetailDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.e("look_vote: ", "${result.toString()}")
                } else Log.e("look_vote", " 통신오류: ${response.message()}")
            }

            override fun onFailure(call: Call<VoteDetailDTO>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })

         */

        /*
        val call4 = RetrofitHelper.getVoteApi().now_vote(QuestionId(2))
        call4.enqueue(object : Callback<VoteStatusDTO>{
            override fun onResponse(call: Call<VoteStatusDTO>, response: Response<VoteStatusDTO>) {
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.e("now_vote", "${result.toString()}")
                } else Log.e("now_vote", "통신오류: ${response.message()}")
            }

            override fun onFailure(call: Call<VoteStatusDTO>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })
        */


        /*
        val call5 = RetrofitHelper.getVoteApi().end_vote(EndVoteDTO(2, true))
        call5.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    Log.e("end_vote", "${response.code()}")
                } else Log.e("end_vote", "통신오류: ${response.message()}")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "${t.message}")
            }

        })
         */

        /*
        val call6 = RetrofitHelper.getVoteApi().list_vote(RoomId(5))
        call6.enqueue(object : Callback<VoteListDTO>{
            override fun onResponse(call: Call<VoteListDTO>, response: Response<VoteListDTO>) {
                if(response.isSuccessful) {
                    val result = response.body()
                    Log.e("list_vote", "${result.toString()}")
                } else Log.e("list_vote", "${response.message()}")
            }

            override fun onFailure(call: Call<VoteListDTO>, t: Throwable) {
                Log.e("list_vote_onFailure", "${t.message}")
            }

        })

         */
    }


    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }
}