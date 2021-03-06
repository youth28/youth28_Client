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
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.DialogPostVoteContentBinding
import com.example.myapplication.dto.vote.VoteMakeDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PostVoteContentDialog: DialogFragment() {
    private lateinit var binding: DialogPostVoteContentBinding

    // 다이얼로그의 버튼이 눌린경우
    var listener: (String) -> Unit = {title -> }

    var date = MutableLiveData<String>()
    var title = MutableLiveData<String>()

    val contentList = arrayListOf(
            MutableLiveData<String>(), MutableLiveData<String>(), MutableLiveData<String>(), MutableLiveData<String>(), MutableLiveData<String>()
    )

    var contentCnt = 0
    var contentCntS = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_post_vote_content, container, false)
        binding.dialog = this

        binding.apply {
            date.value = "날짜: ${datePicker.year}-${datePicker.month+1}-${datePicker.dayOfMonth}"

            datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                date.value = "날짜: ${year}-${monthOfYear+1}-${dayOfMonth}"
                invalidateAll()
            }
        }
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
        contentCnt = 0
        contentCntS = 0

        if (isNotNull(title.value)) {
            showToast("투표 제목을 입력해주세요.")
        } else {
            for (i: Int in 0..4) {
                if(!isNotNull(contentList[i].value))
                    contentCnt ++
            }

            if(contentCnt < 2) {
                showToast("선택지를 2개이상 적어주세요")
            } else {
                for (i:Int in 0 until contentCnt) {
                    if(!isNotNull(contentList[i].value))
                        contentCntS ++
                }
                if (contentCnt != contentCntS) {
                    showToast("선택지는 앞당겨서 작성해주세요!")
                } else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    date.value = date.value!!.substring(4, date.value!!.length)
                    val voteMakeDTO = VoteMakeDTO(title.value.toString(), contentList[0].value.toString(), contentList[1].value.toString(),
                            contentList[2].value.toString(), contentList[3].value.toString(), contentList[4].value.toString(),
                            sdf.format(Date()), date.value.toString(), false, RoomData.roomId, UserData.userNum.toInt())
                    Log.e("voteMakeDTO", "$voteMakeDTO")

                    val call = RetrofitHelper.getVoteApi().question_make(voteMakeDTO = voteMakeDTO)
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Log.e("response: ", response.code().toString())
                                listener.invoke(title.value.toString())
                                dismiss()
                            } else Log.e("response: ", "통신 안됨, ${response.message()}")
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("onFailure", "${t.message}")
                        }

                    })
                }
            }
        }
    }

    fun isNotNull(value: String?): Boolean {
        return (value == null || value == "")
    }


    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

}

