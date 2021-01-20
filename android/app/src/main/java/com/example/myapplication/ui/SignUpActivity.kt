package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.UserDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.editEmail
import kotlinx.android.synthetic.main.activity_sign_up.editPW
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpActivity: AppCompatActivity() {

    val TAG = "SignUpActivity"

    var strId = ""
    var strPW =""
    var strRePW = ""
    var strName = ""

    var isAbleId = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignup.setOnClickListener {
            strId = editEmail.text.toString()
            strPW = editPW.text.toString()
            strRePW = editRePW.text.toString()
            strName = editName.text.toString()

            // region 회원가입
            if(strId == "" || strPW =="" || strRePW == "" || strName == "") {
                if (strId == ""){
                    tvErrIdS.text = "이메일을 입력해주세요."
                    tvErrIdS.visibility = View.VISIBLE
                } else tvErrIdS.visibility = View.GONE
                if (strPW == "") {
                    tvErrPwS.text = "비빌번호를 입력해주세요."
                    tvErrPwS.visibility = View.VISIBLE
                } else tvErrPwS.visibility = View.GONE
                if (strRePW == "") {
                    tvErrRePwS.text = "비밀번호를 다시 입력해주세요"
                    tvErrRePwS.visibility = View.VISIBLE
                } else tvErrRePwS.visibility = View.GONE
                if (strName == "") {
                    tvErrNameS.text = "이름을 입력해주세요"
                    tvErrNameS.visibility = View.VISIBLE
                } else {
                    tvErrNameS.visibility = View.GONE
                }
            }
            else {
                tvErrIdS.visibility = View.GONE
                tvErrPwS.visibility = View.GONE
                tvErrRePwS.visibility = View.GONE
                tvErrNameS.visibility = View.GONE

                // email 중복확인
                if (!isAbleId) {
                    tvErrIdS.text = "중복확인을 해주세요"
                    tvErrIdS.visibility = View.VISIBLE
                } else tvErrIdS.visibility = View.GONE

                // 비밀번호와 비밀번호 확인이 같은지
                if (strPW != strRePW) {
                    tvErrRePwS.text = "비밀번호를 재확인 해주세요"
                    tvErrRePwS.visibility = View.VISIBLE
                } else tvErrRePwS.visibility = View.GONE

                // 비밀번호 형식 확인
                if (!Pattern.matches("^(?=.*\\\\d)(?=.*[~`!@#\$%\\\\^&*()-])(?=.*[a-zA-Z]).{8,16}\$", strPW)) {
                    tvErrPwS.text = "비밀번호 형식은 대소문자 구분, 숫자, 특수문자가 포함된 8~16글자 입니다."
                    tvErrPwS.visibility = View.VISIBLE
                } else tvErrPwS.visibility = View.GONE

                // 모두 통과하면
                if(isAbleId && strPW == strRePW) {
                    tvErrIdS.visibility = View.GONE
                    tvErrPwS.visibility = View.GONE
                    tvErrRePwS.visibility = View.GONE

                    // 회원가입 하기
                    val user = getData()
                    val call = RetrofitHelper.getApiService().register(user)
                    call.enqueue(object : Callback<UserDTO> {
                        override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                            if(response.isSuccessful) {
                                showToast("회원가입 성공")
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                            Log.e(TAG+" Err", "통신안됨: ${t.message}")
                        }

                    })
                }

            }
            // endregion
        }

        btnCheckEmail.setOnClickListener {
            strId = editEmail.text.toString()

            if (strId == "") {
                tvErrIdS.text = "아이디를 비워둘 수 없습니다."
                tvErrIdS.visibility = View.VISIBLE
            }
            // 이메일 형식체크
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(strId).matches()) {
                tvErrIdS.text = "아이디는 Email형식으로 작성해주세요"
                tvErrIdS.visibility = View.VISIBLE
            }
            else {
                tvErrIdS.visibility = View.GONE

                // 중복 확인 200-> 사용가능한 ID, 204-> 중복되는 아이디
                val user = UserDTO(strId)
                val call = RetrofitHelper.getApiService().check_emial(user)
                call.enqueue(object : Callback<UserDTO> {
                    override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                        Log.e(TAG, "성공")
                        val result = response.code()
                        when(result){
                            200 -> {
                                tvErrIdS.visibility = View.GONE
                                showToast("${strId}는 사용가능한 아이디 입니다.")
                                isAbleId = true
                            }
                            204 -> {
                                tvErrIdS.text = "중복되는 아이디 입니다."
                                tvErrIdS.visibility = View.VISIBLE
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                        Log.e(TAG+" Err", "통신안됨: ${t.message}")
                    }

                })
            }
        }

    }

    fun getData(): UserDTO {
        val data = UserDTO(strId, strPW, strName)
        return data
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}