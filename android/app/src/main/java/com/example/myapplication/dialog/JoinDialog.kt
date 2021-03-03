package com.example.myapplication.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.room.JoinRoomDTO
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.DialogJoinBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinDialog(context: Context): DialogFragment() {

    val TAG = "JoinDialog"

    val mainMsg = MutableLiveData<String>()
    var title = ""
    var room_id = 0

    private lateinit var binding:DialogJoinBinding

    override fun onStart() {
        super.onStart()
        // 다이얼로그의 넓이와 높이 지정
        val width = resources.getDimensionPixelSize(R.dimen.join_dialog_width)
        val height = resources.getDimensionPixelSize(R.dimen.join_dialog_height)
        dialog?.window?.setLayout(width, height)
        //dialog?.window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_join, container, false)
        binding.dialog = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mainMsg.value = "${title}에 참여하시겠습니까?"
    }

    fun onYes() {
        val join = JoinRoomDTO(UserData.userNum.toInt(), room_id)
        val call = RetrofitHelper.getRoomApi().room_join(join)
        call.enqueue(object : Callback<JoinRoomDTO> {
            override fun onResponse(call: Call<JoinRoomDTO>, response: Response<JoinRoomDTO>) {
                if (response.isSuccessful) {
                    when (response.code()) {
                        200 -> showToast("${title}에 참가하였습니다.")
                        204 -> showToast("이미 참여된 방입니다.")
                    }
                } else {
                    Log.e("실패", response.message())
                }
            }

            override fun onFailure(call: Call<JoinRoomDTO>, t: Throwable) {
                Log.e(TAG+"ERR", "통신안됨 ${t.message}")
            }

        })
        dismiss()
    }

    fun onNo() {
        dismiss()
    }

    fun showToast(str: String) {
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()
    }
}