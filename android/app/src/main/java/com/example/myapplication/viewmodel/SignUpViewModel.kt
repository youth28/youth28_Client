package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.room.ResponseLogin
import com.example.myapplication.dto.user.UserDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SignUpViewModel: ViewModel() {
    val TAG = "SignUpViewModel"

    // region MutableLiveData
    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val rePassword = MutableLiveData<String>()
    val errMsg = MutableLiveData<String>()

    val errId = MutableLiveData<String>()
    val errPW = MutableLiveData<String>()
    val errRePW = MutableLiveData<String>()
    val errName = MutableLiveData<String>()
    
    val viewErrId = MutableLiveData<Boolean>()
    val viewErrPW = MutableLiveData<Boolean>()
    val viewErrRePW = MutableLiveData<Boolean>()
    val viewErrName = MutableLiveData<Boolean>()
    
    val cbStudy = MutableLiveData<Boolean>()
    val cbWork = MutableLiveData<Boolean>()
    val cbGame = MutableLiveData<Boolean>()
    val cbMusic = MutableLiveData<Boolean>()
    val cbArt = MutableLiveData<Boolean>()
    val cbExercise = MutableLiveData<Boolean>()
    val cbEtc = MutableLiveData<Boolean>()

    val dialog = MutableLiveData(false)
    // endregion

    val onSignUpEvent = SingleLiveEvent<Unit>()
    val onSignUpFailEvent = SingleLiveEvent<Unit>()
    val onCheckEmailEvent = SingleLiveEvent<Unit>()

    var isAbleId = false
    var strField = ""

    fun onSignup() {
        if(isNotNull(email.value) || isNotNull(password.value) || isNotNull(rePassword.value) || isNotNull(name.value)) {
            if (isNotNull(email.value)){
                errId.value= "이메일을 입력해주세요."
                viewErrId.value = true
            } else viewErrId.value = false
            if (isNotNull(password.value)) {
                errPW.value = "비빌번호를 입력해주세요."
                viewErrPW.value = true
            } else viewErrPW.value = false
            if (isNotNull(rePassword.value)) {
                errRePW.value = "비밀번호를 다시 입력해주세요"
                viewErrRePW.value = true
            } else viewErrRePW.value = false
            if (isNotNull(name.value)) {
                errName.value = "이름을 입력해주세요"
                viewErrName.value = true
            } else {
                viewErrName.value = false
            }
        }
        else {
            viewErrId.value = false
            viewErrPW.value = false
            viewErrRePW.value = false
            viewErrName.value = false

            // email 중복확인
            if (!isAbleId) {
                errId.value = "중복확인을 해주세요"
                viewErrId.value = true
            } else viewErrId.value = false

            // 비밀번호와 비밀번호 확인이 같은지
            if (password.value != rePassword.value) {
                errRePW.value = "비밀번호를 재확인 해주세요"
                viewErrRePW.value = true
            } else viewErrRePW.value = false

            // 비밀번호 형식 확인
            if (!Pattern.matches("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[\$@\$!%*#?&]).{6,16}.\$", password.value)) {
                errPW.value = "비밀번호 형식은 대소문자 구분, 숫자, 특수문자가 포함된 6~16글자 입니다."
                viewErrPW.value = true
            } else viewErrPW.value = false

            // 모두 통과하면
            if (isAbleId && password.value == rePassword.value) {
                viewErrId.value = false
                viewErrPW.value = false
                viewErrRePW.value = false

                strField = ""
                if (cbStudy.value == true) strField += "스터디,"
                if (cbWork.value == true) strField += "업무,"
                if (cbGame.value == true) strField += "게임,"
                if (cbMusic.value == true) strField += "음악,"
                if (cbArt.value == true) strField += "미술,"
                if (cbExercise.value == true) strField += "운동(헬스),"
                if (cbEtc.value == true) strField += "기타,"

                if (strField == "") {
                    errMsg.value = "분야를 선택하지 않아 기타로 표시됩니다."
                    cbEtc.value = true
                    strField = "기타,"
                }

                if (strField.isNotEmpty()) {
                    strField = strField.substring(0, strField.length - 1)
                }

                // 회원가입 하기
                dialog.value = true
                val user = getData()
                val call = RetrofitHelper.getUserApi().register(user)
                call.enqueue(object : Callback<ResponseLogin> {
                    override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                        if (response.isSuccessful) {
                            Log.e("UserDataga", response.body().toString())
                            UserData.userId = email.value!!
                            UserData.userPassword = password.value!!

                            onSignUpEvent.call()
                            dialog.value = false
                        } else dialog.value = false
                    }

                    override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                        Log.e(TAG + " Err", "통신안됨: ${t.message}")
                        dialog.value = false
                       // onSignUpFailEvent.call()
                    }

                })
            }
        }
    }

    fun onCheckEmail() {
        if (isNotNull(email.value)) {
            errId.value = "아이디를 비워둘 수 없습니다."
            viewErrId.value = true
        }
        // 이메일 형식체크
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            errId.value = "아이디는 Email형식으로 작성해주세요"
            viewErrId.value = true
        } else {
            viewErrId.value = false

            // 중복 확인 200-> 사용가능한 ID, 204-> 중복되는 아이디
            val user = UserDTO(email.value.toString())
            Log.e(TAG, user.toString())
            val call = RetrofitHelper.getUserApi().check_emial(user)
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    Log.e(TAG, "성공 ${response.code()}")
                    val result = response.code()
                    when (result) {
                        200 -> {
                            viewErrId.value = false
                            onCheckEmailEvent.call()
                            isAbleId = true
                            Log.e(TAG, "200 중복아님")
                        }
                        204 -> {
                            errId.value = "중복되는 아이디 입니다."
                            viewErrId.value = true
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

    fun infoEmail() {
        errMsg.value = "이메일 중복확인이 완료되면\n수정할 수 없습니다."
    }

    fun isNotNull(value: String?): Boolean {
        return (value == null || value == "")
    }

    fun getData(): UserDTO {
        val data = UserDTO(email.value.toString(), password.value.toString(), name.value.toString(), strField)
        return data
    }
}