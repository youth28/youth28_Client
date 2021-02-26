package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    val TAG = "LoginActivity"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    private lateinit var loadDialog: SweetAlertDialog

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userId = preferences.getString("userId", "").toString()
        UserData.userPassword = preferences.getString("userPW", "").toString()

        Log.e(TAG, "hihi ${UserData.userId} ,,, ${UserData.userPassword}")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        // email, PW 값이 null이 아니라면 자동 로그인 하기
        viewModel.autoLogin()

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
                editor.putString("userPW", UserData.userPassword)
                editor.putString("userNum", UserData.userNum)
                editor.putString("userProfile", UserData.userProfile)
                editor.putString("userName", UserData.userName)
                editor.apply()
                finish()
            })
            onAutoLoginEvent.observe(this@LoginActivity, {
                val dialog = SweetAlertDialog(this@LoginActivity , SweetAlertDialog.SUCCESS_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#36b8ff")
                dialog.titleText = "로그인이 완료되었습니다."
                dialog.show()

                loadDialog.dismiss()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
            onAutoLoginFailEvent.observe(this@LoginActivity, {
                val dialog = SweetAlertDialog(this@LoginActivity , SweetAlertDialog.ERROR_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#36b8ff")
                dialog.titleText = "통신 안됨."
                dialog.setCancelable(false)
                dialog.show()
            })
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}