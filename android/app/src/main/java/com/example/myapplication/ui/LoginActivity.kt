package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    var strId = ""
    var strPW = ""

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        strId = preferences.getString("userId", null).toString()
        strPW = preferences.getString("userId", null).toString()

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
                tvErrPw.visibility = View.VISIBLE

                // 로그인 통신 코드

                //성공할시
                showToast("자동 로그인 등록")

                preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString("userId", strId)
                editor.putString("userPW", strPW)
                editor.putString("userName", "response.body().getName")
                editor.putString("userProfile", "image")

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

                // 아이디나 비번이 일치하지 않는다면 code -> 204
                tvErrPw.text = "아이디나 비밀번호가 일치하지 않습니다."
                tvErrPw.visibility = View.VISIBLE
            }
        }

        // null이 아니면 자동로그인
        if (strId != null && strPW!= null){
            // 로그인 통신 코드
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}