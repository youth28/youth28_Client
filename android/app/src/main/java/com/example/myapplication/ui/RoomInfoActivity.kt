package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.RoomId
import com.example.myapplication.DTO.RoomMakeDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_room_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoActivity : AppCompatActivity() {

    val TAG = "RoomInfoActivity"

    var title = ""
    var maxPro = 0
    var field = ""
    var profile = ""
    var room_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_info)

        settingUi()

        btnUpdateRoom.setOnClickListener {
            room_id = 3
            val intent = Intent(this@RoomInfoActivity, RoomModifyActivity::class.java)
            intent.putExtra("roomTitle", title)
            intent.putExtra("roomMax", maxPro)
            intent.putExtra("roomField", field)
            intent.putExtra("roomId", room_id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        settingUi()
    }

    fun settingUi () {
        val room = RoomId(2)
        val call = RetrofitHelper.getApiService().room_info(room)
        call.enqueue(object : Callback<RoomMakeDTO> {
            override fun onResponse(call: Call<RoomMakeDTO>, response: Response<RoomMakeDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        field = response.body()!!.field
                        title = response.body()!!.title
                        profile = response.body()!!.profile
                        maxPro = response.body()!!.maxPeo

                        tvRName.text = title
                        peRson.text = "${maxPro}명"

                        // region checkBox 설정하기
                        field = field.substring(0, field.length -1 )
                        Log.e(TAG+ "field", field)

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
                        // endregion

                        Log.e(TAG, "정보 받아오기 완료")

                    }
                }
            }

            override fun onFailure(call: Call<RoomMakeDTO>, t: Throwable) {
                Log.e(TAG+" Err", "통신안됨: ${t.message}")
            }

        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}