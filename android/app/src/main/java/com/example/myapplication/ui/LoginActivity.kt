package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.ResponseLogin
import com.example.myapplication.DTO.UserDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {

    val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding

    var email = ""
    var PW = ""

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.activity = this

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        email = preferences.getString("userId", "").toString()
        PW = preferences.getString("userPW", "").toString()

        Log.e(TAG, "$email, $PW")

        // email, PW 값이 null이 아니라면 자동 로그인 하기
        autoLogin()
    }

    fun autoLogin() {
        if (email !="null" && PW !="null"){
            val user = getData()
            Log.e(TAG, "$email  $PW")
            val call = RetrofitHelper.getApiService().login(user)
            Log.e(TAG, user.toString())
            call.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        val result = response.code()
                        when (result) {
                            200 -> {
                                //성공할시
                                showToast("자동 로그인 했습니다.")

                                preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            204 -> {
                                binding.tvErrPw.text = "아이디나 비밀번호가 일치하지 않습니다."
                                binding.tvErrPw.visibility = View.VISIBLE
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

    fun onSignup(view: View) {
        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun onLogin(view: View) {
        binding.apply {
            if (email == "" || PW == "") {
                if (email == "") {
                    tvErrId.text = "Email을 작성해주세요"
                    tvErrId.visibility = View.VISIBLE
                } else {
                    tvErrId.visibility = View.GONE
                }
                if (PW == "") {
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
                                    editor.putString("userId", email)
                                    editor.putString("userPW", PW)
                                    editor.putString("userNum", response.body()!!.user_id)
                                    Log.e(TAG + " userNum", response.body()!!.user_id)
                                    editor.putString("userName", response.body()!!.name)
                                    editor.putString("userProfile", "image")
                                    editor.apply()

                                    Log.e(TAG + " Response", response.body().toString())

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
                        Log.e(TAG + " Err", "통신안됨: ${t.message}")
                    }

                })
            }
        }
    }

    fun getData(): UserDTO {
        val data = UserDTO(email, PW)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}