package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomInfoDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRoomInfoBinding
import kotlinx.android.synthetic.main.activity_room_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoActivity : AppCompatActivity() {

    val TAG = "RoomInfoActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityRoomInfoBinding

    var title = ""
    var maxPro = 0
    var field = ""
    var profile = ""
    var room_id = 0
    var roomManager = 0

    var maxPeo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_info)
        binding.info = this

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        room_id = intent.getIntExtra("roomId", 0)
        Log.e(TAG, "room_id: ${room_id}")

        settingUi()
    }

    override fun onResume() {
        super.onResume()

        settingUi()
    }

    fun onUpdateRoom(view: View) {
//        if (roomManager == preferences.getString("userNum", "0")!!.toInt()) {
            val intent = Intent(this@RoomInfoActivity, RoomModifyActivity::class.java)
            intent.putExtra("roomTitle", title)
            intent.putExtra("roomMax", maxPro)
            intent.putExtra("roomField", field)
            intent.putExtra("roomId", room_id)
            intent.putExtra("roomProfile", profile)
            startActivity(intent)
//        } else {
//            showToast("방에대한 수정권한이 없습니다.")
//            Log.e(TAG, "userId=${preferences.getString("userNum","0")}, roomManager=${roomManager}")
//        }
    }

    fun settingUi () {

        title = "안녕"
        maxPro = 5
        maxPeo = "${maxPro}명"
        field = "스터디,게임,미술"
        profile = "http://d3c30c5e052c.ngrok.io/uploads/98eed633ca782547430c1768572e1cdb.png"

        val arrayList = field.split(",")
        for (value in arrayList) {
            when (value) {
                "스터디" -> binding.cbStudy.isChecked = true
                "게임" -> binding.cbGame.isChecked = true
                "업무" -> binding.cbWork.isChecked = true
                "음악" -> binding.cbMusic.isChecked = true
                "미술" -> binding.cbArt.isChecked = true
                "운동(헬스)" -> binding.cbExercise.isChecked = true
                "기타" -> binding.cbEtc.isChecked = true
            }
        }
        val room = RoomId(room_id)
        val call = RetrofitHelper.getApiService().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        field = response.body()!!.field
                        title = response.body()!!.title
                        profile = response.body()!!.profile
                        maxPro = response.body()!!.maxPeo
                        roomManager = response.body()!!.room_manager

                        Log.e(TAG, "room_manager: ${roomManager}")

                        maxPeo = "${maxPro}명"

                        // region checkBox 설정하기
                        field = field.substring(0, field.length -1 )
                        Log.e(TAG+ "field", field)

                        val arrayList = field.split(",")
                        for (value in arrayList) {
                            when (value) {
                                "스터디" -> binding.cbStudy.isChecked = true
                                "게임" -> binding.cbGame.isChecked = true
                                "업무" -> binding.cbWork.isChecked = true
                                "음악" -> binding.cbMusic.isChecked = true
                                "미술" -> binding.cbArt.isChecked = true
                                "운동(헬스)" -> binding.cbExercise.isChecked = true
                                "기타" -> binding.cbEtc.isChecked = true
                            }
                        }
                        // endregion

                        Log.e(TAG, "정보 받아오기 완료")

                    }
                }
            }

            override fun onFailure(call: Call<RoomInfoDTO>, t: Throwable) {
                Log.e(TAG+" Err", "통신안됨: ${t.message}")
            }

        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}