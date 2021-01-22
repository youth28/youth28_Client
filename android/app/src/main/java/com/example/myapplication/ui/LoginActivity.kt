package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.ResponseLogin
import com.example.myapplication.DTO.UserDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {

    val TAG = "LoginActivity"

    var strId = ""
    var strPW = ""

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        strId = preferences.getString("userId", null).toString()
        strPW = preferences.getString("userPW", null).toString()

        // strId, strPw 값이 null이 아니라면 자동 로그인 하기
        if (strId !=null || strPW !=null){
            val user = getData()
            Log.e(TAG, "$strId  $strPW")
            val call = RetrofitHelper.getApiService().login(user)
            Log.e(TAG, user.toString())
            call.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        val result = response.code()
                        when (result) {
                            200 -> {
                                //성공할시
                                showToast("자동 로그인 등록")

                                preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            204 -> {
                                tvErrPw.text = "아이디나 비밀번호가 일치하지 않습니다."
                                tvErrPw.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    Log.e(TAG+" Err", "통신안됨: ${t.message}")
                }

            })
        }

        btnLogin.setOnClickListener {
            strId = editEmail.text.toString()
            strPW = editPW.text.toString()

            if (strId == "" || strPW ==""){
                if (strId == "") {
                    tvErrId.visibility = View.VISIBLE
                    tvErrId.text = "Email을 작성해주세요"
                } else {
                    tvErrId.visibility = View.GONE
                }
                if (strPW == ""){
                    tvErrPw.visibility = View.VISIBLE
                    tvErrPw.text = "비밀번호를 작성해주세요"
                } else {
                    tvErrPw.visibility = View.GONE
                }
            } else {
                tvErrId.visibility = View.GONE
                tvErrPw.visibility = View.GONE

                // 로그인 통신 코드
                val user = getData()
                val call = RetrofitHelper.getApiService().login(user)
                Log.e(TAG, user.toString())
                call.enqueue(object : Callback<ResponseLogin> {
                    override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                        if (response.isSuccessful) {
                            val result = response.code()
                            when (result) {
                                200 -> {
                                    //성공할시
                                    showToast("자동 로그인 등록")

                                    preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
                                    val editor = preferences.edit()
                                    editor.putString("userId", strId)
                                    editor.putString("userPW", strPW)
                                    editor.putString("userNum", response.body()!!.user_id)
                                    Log.e(TAG+" userNum", response.body()!!.user_id)
                                    editor.putString("userName", response.body()!!.name)
                                    editor.putString("userProfile", "image")
                                    editor.apply()

                                    Log.e(TAG+" Response", response.body().toString())

                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                204 -> {
                                    tvErrPw.text = "아이디나 비밀번호가 일치하지 않습니다."
                                    tvErrPw.visibility = View.VISIBLE
                                    Log.e(TAG, "204")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        Log.e(TAG+" Err", "통신안됨: ${t.message}")
                    }

                })

            }
        }

        tvSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        // null이 아니면 자동로그인
        if (strId != null && strPW!= null){
            // 로그인 통신 코드
            val user = getData()
        }
    }

    fun getData(): UserDTO {
        val data = UserDTO(strId, strPW)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}