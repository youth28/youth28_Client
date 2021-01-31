package com.example.myapplication.ui

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.UserModify
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_modify.*
import kotlinx.android.synthetic.main.activity_modify.editEmail
import kotlinx.android.synthetic.main.activity_modify.editName
import kotlinx.android.synthetic.main.activity_modify.editPW
import kotlinx.android.synthetic.main.activity_room_modify.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyActivity: AppCompatActivity() {

    val TAG = "ModifyActivity"

    internal lateinit var preferences: SharedPreferences

    var user_id: Int = 0
    var strName = ""
    var strPW = ""
    var strEmail = ""
    var imgProfile = "ex"
    var strField = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        editName.setText(preferences.getString("userName", ""))

        if (intent.hasExtra("field")) {
            strField = intent.getStringExtra("field").toString()
            val arrayList = strField.split(",")
            for (value in arrayList) {
                when (value) {
                    "스터디" -> mStudy.isChecked = true
                    "게임" -> mGame.isChecked = true
                    "업무" -> mWork.isChecked = true
                    "음악" -> mMusic.isChecked = true
                    "미술" -> mArt.isChecked = true
                    "운동(헬스)" -> mExercise.isChecked = true
                    "기타" -> mEtc.isChecked = true
                }
            }
        }

        btnSave.setOnClickListener {

            strName = editName.text.toString()
            strPW = editPW.text.toString()
            strEmail = editEmail.text.toString()
            imgProfile = preferences.getString("userProfile", "").toString()
            user_id = preferences.getString("userNum", "0")!!.toInt()
            strField = ""

            if (strName == "") {
                showToast("이름을 입력해주세요")
            } else if (strEmail == "") {
                showToast("이메일을 입력해주세요")
            } else if (strPW == "") {
                showToast("현재 비밀번호를 입력해주세요")
            } else if (strEmail != "" && strPW != "") {
                if (preferences.getString("userId", "") != strEmail) {
                    showToast("일치하지 않는 아이디 입니다.")
                }
                if (preferences.getString("userPW", "") != strPW) {
                    showToast("일지하지 않는 비밀번호 입니다.")
                }

                if (preferences.getString("userId", "") == strEmail && preferences.getString("userPW", "") == strPW) {

                    if (mStudy.isChecked) strField += "${mStudy.text},"
                    if (mWork.isChecked) strField += "${mWork.text},"
                    if (mGame.isChecked) strField += "${mGame.text},"
                    if (mMusic.isChecked) strField += "${mMusic.text},"
                    if (mArt.isChecked) strField += "${mArt.text},"
                    if (mExercise.isChecked) strField += "${mExercise.text},"
                    if (mEtc.isChecked) strField += "${mEtc.text},"

                    if(strField.length >0) {
                        strField = strField.substring(0, strField.length - 1)
                    }
                    
                    val user = getData()
                    val call = RetrofitHelper.getApiService().modify(user)
                    call.enqueue(object : Callback<UserModify>{
                        override fun onResponse(call: Call<UserModify>, response: Response<UserModify>) {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    val editor = preferences.edit()
                                    editor.putString("userName", strName)
                                    editor.putString("userProfile", imgProfile)
                                    showToast("수정되었습니다.")
                                    finish()
                                }
                            } else Log.e(TAG, response.message())
                        }
                        override fun onFailure(call: Call<UserModify>, t: Throwable) {
                            Log.e(TAG, "통신안됨: ${t.message}")
                        }

                    })
                }
            }

        }
    }

    fun getData(): UserModify {
        val data = UserModify(user_id, strName, imgProfile, strField)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}