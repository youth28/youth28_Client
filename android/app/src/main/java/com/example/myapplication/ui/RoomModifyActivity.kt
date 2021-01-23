package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.RoomModifyDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_room_modify.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomModifyActivity : AppCompatActivity() {

    val TAG = "RoomModifyActivity"

    var title = ""
    var maxPro = 0
    var field = ""
    var profile = ""
    var room_id = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_modify)

        if (intent.hasExtra("roomTitle")) {
            Log.e(TAG, "intent 있음")
            editRname.setText(intent.getStringExtra("roomTitle"))
            peRson.text = intent.getStringExtra("roomMax")
            field = intent.getStringExtra("roomField").toString()
            room_id = intent.getIntExtra("roomId", 0)

            val arrayList = field.split(",")
            for (value in arrayList) {
                when (value) {
                    "스터디" -> cbStudy.isChecked = true
                    "게임" -> cbGame.isChecked = true
                    "업무" -> cbWork.isChecked = true
                    "음악" -> cbMusic.isChecked = true
                    "미술" -> cbArt.isChecked = true
                    "운동(헬스)" -> cbExercise.isChecked = true
                    "기타" -> cbEtc.isChecked = true
                }
            }
        }

        btnSub.setOnClickListener {
            peRson.text = "${Integer.parseInt(peRson.text.toString()) - 1}"
            Log.e(TAG+ "sub", peRson.text.toString())
        }

        btnPlu.setOnClickListener {
            peRson.text = "${Integer.parseInt(peRson.text.toString()) + 1}"
            Log.e(TAG+ "plu", peRson.text.toString())
        }

        btnModifyRoom.setOnClickListener {
            title = editRname.text.toString()
            maxPro = Integer.parseInt(peRson.text.toString())
            Log.e(TAG, "$maxPro")

            // region checkBox String으로
            if (cbStudy.isChecked) field += "${cbStudy.text},"
            if (cbWork.isChecked) field += "${cbWork.text},"
            if (cbGame.isChecked) field += "${cbGame.text},"
            if (cbMusic.isChecked) field += "${cbMusic.text},"
            if (cbArt.isChecked) field += "${cbArt.text},"
            if (cbExercise.isChecked) field += "${cbExercise.text},"
            if (cbEtc.isChecked) field += "${cbEtc.text},"
            // endregion

            Log.e(TAG, "checkBox: $field 선택됨")

            val room = getData()
            val call = RetrofitHelper.getApiService().modify_room(room)
            call.enqueue(object : Callback<RoomModifyDTO> {
                override fun onResponse(call: Call<RoomModifyDTO>, response: Response<RoomModifyDTO>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            showToast("성공적으로 방을 수정하였습니다.")
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<RoomModifyDTO>, t: Throwable) {
                    Log.e(TAG, "통신안됨 ${t.message}")
                }

            })

        }


    }

    fun getData(): RoomModifyDTO {
        val data = RoomModifyDTO(room_id, title, maxPro, field, profile)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}