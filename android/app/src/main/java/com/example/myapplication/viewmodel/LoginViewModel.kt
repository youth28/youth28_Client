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

class LoginViewModel: ViewModel() {
    val TAG = "LoginViewModel"

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errId = MutableLiveData<String>()
    val errPW = MutableLiveData<String>()
    val viewErrId = MutableLiveData<Boolean>()
    val viewErrPW = MutableLiveData<Boolean>()

    val onSignUpEvent = SingleLiveEvent<Unit>()
    val onLoginEvent = SingleLiveEvent<Unit>()
    val onAutoLoginEvent = SingleLiveEvent<Unit>()
    val onAutoLoginFailEvent = SingleLiveEvent<Unit>()

    fun onSignup() {
        onSignUpEvent.call()
    }

    fun onLogin() {
        if (email.value == null || password.value == null || email.value == "" || password.value == "") {
            if (email.value == "" || email.value == null) {
                errId.value = "Email을 작성해주세요"
                viewErrId.value = true
            } else {
                viewErrId.value = false
            }
            if (password.value == "" || password.value == null) {
                viewErrPW.value = true
                errPW.value = "비밀번호를 작성해주세요"
            } else {
                viewErrPW.value = false
            }
        } else {
            viewErrId.value = false
            viewErrPW.value = false

            // 로그인 통신 코드
            val user = getData()
            val call = RetrofitHelper.getUserApi().login(user)
            Log.e(TAG, user.toString())
            call.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        val result = response.code()
                        when (result) {
                            200 -> {
                                //성공할시
                                UserData.userId = email.value!!
                                UserData.userPassword = password.value!!
                                UserData.userNum = response.body()!!.user_id
                                UserData.userName = response.body()!!.name
                                UserData.userProfile = "img"

                                Log.e("$TAG Response", response.body().toString())

                                onLoginEvent.call()
                            }
                            204 -> {
                                errPW.value = "아이디나 비밀번호가 일치하지 않습니다."
                                viewErrPW.value = true
                                Log.e(TAG, "204")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    Log.e("$TAG Err", "통신안됨: ${t.message}")
                    onAutoLoginFailEvent.call()
                }

            })
        }
    }

    fun autoLogin() {
        email.value = UserData.userId
        password.value = UserData.userPassword
        Log.e(TAG, "${email.value}, ${password.value}")

        if (email.value != null && email.value != "" && password.value !=  null && password.value != ""){
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
                                onAutoLoginEvent.call()
                            }
                            204 -> {
                                errPW.value = "아이디나 비밀번호가 일치하지 않습니다."
                                viewErrPW.value = true
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    Log.e(TAG+" Err", "통신안됨: ${t.message}")
                    onAutoLoginFailEvent.call()
                }

            })
        }
    }

    fun getData(): UserDTO {
        val data = UserDTO(email.value!!, password.value!!)
        return data
    }

}