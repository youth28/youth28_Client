package com.example.myapplication.viewmodel

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ResponseLogin
import com.example.myapplication.dto.UserDTO
import com.example.myapplication.event.SingleLiveEvent
import com.example.myapplication.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    val TAG = "LoginViewModel"
    internal lateinit var preferences: SharedPreferences

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errId = MutableLiveData<String>()
    val errPW = MutableLiveData<String>()
    val viewErrId = MutableLiveData<Boolean>()
    val viewErrPW = MutableLiveData<Boolean>()

    val onSignUpEvent = SingleLiveEvent<Unit>()
    val onLoginEvent = SingleLiveEvent<Unit>()

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
            val call = RetrofitHelper.getApiService().login(user)
            Log.e(TAG, user.toString())
            call.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                    if (response.isSuccessful) {
                        val result = response.code()
                        when (result) {
                            200 -> {
                                //성공할시
                                UserData.userId = email.value!!
                                UserData.userpassword = email.value!!
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
                }

            })
        }
    }

    fun getData(): UserDTO {
        val data = UserDTO(email.value!!, password.value!!)
        return data
    }

}