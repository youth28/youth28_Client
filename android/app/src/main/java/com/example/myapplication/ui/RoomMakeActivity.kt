package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.RoomMakeDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_room_make.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomMakeActivity: AppCompatActivity() {

    val TAG = "RoomMakeActivity"

    internal lateinit var preferences: SharedPreferences

    var title = ""
    var maxPro = 0
    var field = ""
    var profile = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_make)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        btnSub.setOnClickListener {
            peRson.text = "${Integer.parseInt(peRson.text.toString()) - 1}"
            Log.e(TAG+ "sub", peRson.text.toString())
        }

        btnPlu.setOnClickListener {
            peRson.text = "${Integer.parseInt(peRson.text.toString()) + 1}"
            Log.e(TAG+ "plu", peRson.text.toString())
        }

        btnMakeRoom.setOnClickListener {
            title = editRname.text.toString()
            maxPro = Integer.parseInt(peRson.text.toString())
            Log.e(TAG+ "maxpro", "$maxPro")

            if (cbStudy.isChecked) field += "${cbStudy.text},"
            if (cbWork.isChecked) field += "${cbWork.text},"
            if (cbGame.isChecked) field += "${cbGame.text},"
            if (cbMusic.isChecked) field += "${cbMusic.text},"
            if (cbArt.isChecked) field += "${cbArt.text},"
            if (cbExercise.isChecked) field += "${cbExercise.text},"
            if (cbEtc.isChecked) field += "${cbEtc.text},"

            Log.e(TAG, "checkBox: $field 선택됨")

            val room = getData()
            val call = RetrofitHelper.getApiService().make_room(room)
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


    }

    fun getData(): RoomMakeDTO {
        val data = RoomMakeDTO(title, maxPro, field, profile, preferences.getString("userNum", "0")!!.toInt())
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}