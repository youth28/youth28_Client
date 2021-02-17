package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.dto.ResponseLogin
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpActivity: AppCompatActivity() {

    val TAG = "SignUpActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    var email = ""
    var PW =""
    var rePW = ""
    var name = ""
    var strField = ""

    var isAbleId = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(SignUpViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        with(viewModel) {
            onSignUpEvent.observe(this@SignUpActivity, {
                val intent = Intent(this@SignUpActivity, ModifyProfileActivity::class.java)
                intent.putExtra("mode", "1")
                startActivity(intent)
                Log.e(TAG, "signUP")
                val editor = preferences.edit()
                editor.putString("userId", UserData.userId)
                editor.putString("userPW", UserData.userpassword)
                editor.putString("userNum", UserData.userNum)
                editor.putString("userProfile", UserData.userProfile)
                editor.putString("userName", UserData.userName)
                editor.apply()
                finish()
            })

            onCheckEmailEvent.observe(this@SignUpActivity, {
                showToast("${email}는 사용가능한 아이디 입니다.")
            })
        }

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}