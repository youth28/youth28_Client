package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomMakeDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRoomMakeBinding
import kotlinx.android.synthetic.main.activity_room_make.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomMakeActivity: AppCompatActivity() {

    val TAG = "RoomMakeActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivityRoomMakeBinding

    var title = ObservableField<String>()
    var maxPro = 0
    var field = ""
    var profile = ""

    var maxPeo = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_make)
        binding.activity = this@RoomMakeActivity

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
    }

    fun onCreateRoom(view: View) {
        Log.e(TAG+ "maxpro", "$maxPro")

        field = ""
        if (binding.cbStudy.isChecked) field += "${binding.cbStudy.text},"
        if (binding.cbWork.isChecked) field += "${binding.cbWork.text},"
        if (binding.cbGame.isChecked) field += "${binding.cbGame.text},"
        if (binding.cbMusic.isChecked) field += "${binding.cbMusic.text},"
        if (binding.cbArt.isChecked) field += "${binding.cbArt.text},"
        if (binding.cbExercise.isChecked) field += "${binding.cbExercise.text},"
        if (binding.cbEtc.isChecked) field += "${binding.cbEtc.text},"

        Log.e(TAG, "checkBox: $field 선택됨")

        val room = getData()
        Log.e("roomMake", room.toString())
        val call = RetrofitHelper.getRoomApi().make_room(room)
        call.enqueue(object : Callback<RoomMakeDTO> {
            override fun onResponse(call: Call<RoomMakeDTO>, response: Response<RoomMakeDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        showToast("성공적으로 방을 만들었습니다. 사이드 메뉴에서 확인해주세요")
                        finish()
                    }
                } else Log.e(TAG, "RoomMake: ${response.message()}")
            }

            override fun onFailure(call: Call<RoomMakeDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨 ${t.message}")
            }

        })
    }

    fun onSub(view: View) {
        maxPeo = "${--maxPro}"
        binding.invalidateAll()
    }

    fun onPlu(view: View) {
        maxPeo = "${++maxPro}"
        binding.invalidateAll()
    }

    fun getData(): RoomMakeDTO {
        val data = RoomMakeDTO(title.get().toString(), maxPro, field, profile, preferences.getString("userNum", "0")!!.toInt())
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}