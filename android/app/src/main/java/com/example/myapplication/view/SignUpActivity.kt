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
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity: AppCompatActivity() {

    val TAG = "SignUpActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    var email = ""
    var name = ""

    private lateinit var loadDialog: SweetAlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(SignUpViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        setDialog()

        with(viewModel) {
            onSignUpEvent.observe(this@SignUpActivity, {
                val intent = Intent(this@SignUpActivity, ModifyProfileActivity::class.java)
                intent.putExtra("mode", "1")
                startActivity(intent)
                Log.e(TAG, "signUP")
                val editor = preferences.edit()
                editor.putString("userId", UserData.userId)
                editor.putString("userPW", UserData.userPassword)
                editor.putString("userNum", UserData.userNum)
                editor.putString("userProfile", UserData.userProfile)
                editor.putString("userName", UserData.userName)
                editor.apply()

                finish()
            })

            onSignUpFailEvent.observe(this@SignUpActivity, {
                val dialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.ERROR_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#FF0000")
                dialog.titleText = "로그인에 실패하였습니다."
                dialog.show()
            })

            onCheckEmailEvent.observe(this@SignUpActivity, {
                val dialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.SUCCESS_TYPE)
                dialog.progressHelper.barColor = Color.parseColor("#FF0000")
                dialog.titleText = "사용가능한 이메일입니다."
                dialog.show()
            })

            dialog.observe(this@SignUpActivity, {
                if (it) {
                    loadDialog.show()
                    Log.e("gg", "회원가입중")
                } else {
                    loadDialog.dismiss()
                    Log.e("GG", "회원가입 완료!")
                }
            })
        }

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

    }

    fun setDialog() {
        loadDialog = SweetAlertDialog(this@SignUpActivity, SweetAlertDialog.PROGRESS_TYPE)
        loadDialog.progressHelper.barColor = Color.parseColor("#36b8ff")
        loadDialog.titleText = "회원가입중..."
        loadDialog.setCancelable(false)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}