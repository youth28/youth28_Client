package com.example.myapplication.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.viewmodel.SignUpViewModel
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity: AppCompatActivity() {

    val TAG = "SignUpActivity"

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

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
                binding.editEmail.isEnabled = false
            })

            dialog.observe(this@SignUpActivity, {
                if (it) {
                    loadDialog.show()
                } else {
                    loadDialog.dismiss()
                }
            })

            errMsg.observe(this@SignUpActivity, {
                showToast(it)
            })
        }
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