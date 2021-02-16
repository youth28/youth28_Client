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
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ResponseLogin
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {

    val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    var email = ""
    var PW = ""

    private lateinit var loadDialog: SweetAlertDialog

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.activity = viewModel
        binding.executePendingBindings()

        // email, PW 값이 null이 아니라면 자동 로그인 하기
        autoLogin()

        with(viewModel) {
            onSignUpEvent.observe(this@LoginActivity, {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
                Log.e(TAG, "signUP")
            })
            onLoginEvent.observe(this@LoginActivity, {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                Log.e(TAG, "${UserData.toStringData()}")
                val editor = preferences.edit()
                editor.putString("userId", UserData.userId)
                editor.putString("userPW", UserData.userpassword)
                editor.putString("userNum", UserData.userNum)
                editor.putString("userProfile", UserData.userProfile)
                editor.putString("userName", UserData.userName)
                editor.apply()
            })
        }
    }

    fun autoLogin() {
        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        email = preferences.getString("userId", "").toString()
        PW = preferences.getString("userPW", "").toString()
        Log.e(TAG, "$email, $PW")

        if (true){
            val user = getData()
            Log.e(TAG, "auto login")

            val call = RetrofitHelper.getUserApi().login(user)
            Log.e(TAG+"g", user.toString())
            call.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        val result = response.code()
                        when (result) {
                            200 -> {

                                // 성공할 시
                                preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

                                // SweetDialog 사용
                                val dialog = SweetAlertDialog(this@LoginActivity , SweetAlertDialog.SUCCESS_TYPE)
                                dialog.progressHelper.barColor = Color.parseColor("#36b8ff")
                                dialog.titleText = "로그인이 완료되었습니다."
                                dialog.setCancelable(false)
                                dialog.show()

                                loadDialog.dismiss()

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
                    loadDialog.dismiss()
                    val dialog = SweetAlertDialog(this@LoginActivity , SweetAlertDialog.ERROR_TYPE)
                    dialog.progressHelper.barColor = Color.parseColor("#36b8ff")
                    dialog.titleText = "로그인 실패."
                    dialog.setCancelable(false)
                    dialog.show()

                }

            })
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