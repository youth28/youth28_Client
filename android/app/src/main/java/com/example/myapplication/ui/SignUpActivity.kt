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
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySignUpBinding
import com.example.myapplication.dto.ResponseLogin
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpActivity: AppCompatActivity() {

    val TAG = "SignUpActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivitySignUpBinding

    var email = ""
    var PW =""
    var rePW = ""
    var name = ""
    var strField = ""

    var isAbleId = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        binding.activity = this@SignUpActivity

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

    }

    fun onSignup(view: View) {
        val intent = Intent(this@SignUpActivity, ModifyProfileActivity::class.java)
        intent.putExtra("mode", "1")
        startActivity(intent)

        binding.apply {

            // region 회원가입
            if(email == "" || PW =="" || rePW == "" || name == "") {
                if (email == ""){
                    tvErrId.text= "이메일을 입력해주세요."
                    tvErrId.visibility = View.VISIBLE
                } else tvErrId.visibility = View.GONE
                if (PW == "") {
                    tvErrPw.text = "비빌번호를 입력해주세요."
                    tvErrPw.visibility = View.VISIBLE
                } else tvErrPw.visibility = View.GONE
                if (rePW == "") {
                    tvErrRePw.text = "비밀번호를 다시 입력해주세요"
                    tvErrRePw.visibility = View.VISIBLE
                } else tvErrRePw.visibility = View.GONE
                if (name == "") {
                    tvErrName.text = "이름을 입력해주세요"
                    tvErrName.visibility = View.VISIBLE
                } else {
                    tvErrName.visibility = View.GONE
                }
            }
            else {
                tvErrId.visibility = View.GONE
                tvErrPw.visibility = View.GONE
                tvErrRePw.visibility = View.GONE
                tvErrName.visibility = View.GONE

                // email 중복확인
                if (!isAbleId) {
                    tvErrId.text = "중복확인을 해주세요"
                    tvErrId.visibility = View.VISIBLE
                } else tvErrId.visibility = View.GONE

                // 비밀번호와 비밀번호 확인이 같은지
                if (PW != rePW) {
                    tvErrRePw.text = "비밀번호를 재확인 해주세요"
                    tvErrRePw.visibility = View.VISIBLE
                } else tvErrRePw.visibility = View.GONE

                // 비밀번호 형식 확인
                if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{6,16}.\$", PW)) {
                    tvErrPw.text = "비밀번호 형식은 대소문자 구분, 숫자, 특수문자가 포함된 6~16글자 입니다."
                    tvErrPw.visibility = View.VISIBLE
                } else tvErrPw.visibility = View.GONE

                // 모두 통과하면
                if (isAbleId && PW == rePW && Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{6,16}.\$", PW)) {
                    tvErrId.visibility = View.GONE
                    tvErrPw.visibility = View.GONE
                    tvErrRePw.visibility = View.GONE

                    strField = ""

                    if (cbStudy.isChecked) strField += "${cbStudy.text},"
                    if (cbWork.isChecked) strField += "${cbWork.text},"
                    if (cbGame.isChecked) strField += "${cbGame.text},"
                    if (cbMusic.isChecked) strField += "${cbMusic.text},"
                    if (cbArt.isChecked) strField += "${cbArt.text},"
                    if (cbExercise.isChecked) strField += "${cbExercise.text},"
                    if (cbEtc.isChecked) strField += "${cbEtc.text},"

                    if (strField.length > 0) {
                        strField = strField.substring(0, strField.length - 1)
                    }

                    Log.e("field", strField)

                    // 회원가입 하기
                    val user = getData()
                    Log.e(TAG, user.toString())
                    val call = RetrofitHelper.getUserApi().register(user)
                    call.enqueue(object : Callback<ResponseLogin> {
                        override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                            if (response.isSuccessful) {
                                showToast("회원가입 성공")

                                preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("userId", email)
                                editor.putString("userPW", PW)
                                editor.putString("userNum", response.body()!!.user_id)
                                Log.e(TAG + " userNum", response.body()!!.user_id)
                                editor.putString("userName", response.body()!!.name)
                                editor.putString("userProfile", "image")
                                editor.apply()

                                val intent = Intent(this@SignUpActivity, ModifyProfileActivity::class.java)
                                intent.putExtra("mode", "1")
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                            Log.e(TAG + " Err", "통신안됨: ${t.message}")
                        }

                    })
                }
            }
        }
    }

    fun onCheckEmail(view: View) {
        binding.apply {
            Log.e("tag", "checkEmail")

            if (email == "") {
                tvErrId.text = "아이디를 비워둘 수 없습니다."
                tvErrId.visibility = View.VISIBLE
            }
            // 이메일 형식체크
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvErrId.text = "아이디는 Email형식으로 작성해주세요"
                tvErrId.visibility = View.VISIBLE
            } else {
                tvErrId.visibility = View.GONE

                // 중복 확인 200-> 사용가능한 ID, 204-> 중복되는 아이디
                val user = UserDTO(email)
                Log.e(TAG, user.toString())
                val call = RetrofitHelper.getUserApi().check_emial(user)
                call.enqueue(object : Callback<UserDTO> {
                    override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                        Log.e(TAG, "성공 $user")
                        val result = response.code()
                        when (result) {
                            200 -> {
                                tvErrId.visibility = View.GONE
                                showToast("${email}는 사용가능한 아이디 입니다.")
                                isAbleId = true
                                Log.e(TAG, "200 중복아님")
                            }
                            204 -> {
                                tvErrId.text = "중복되는 아이디 입니다."
                                tvErrId.visibility = View.VISIBLE
                                Log.e(TAG, "204 중복됨")
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                        Log.e(TAG + " Err", "통신안됨: $t")
                    }

                })
            }
        }
    }

    fun getData(): UserDTO {
        val data = UserDTO(email, PW, name, strField)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}