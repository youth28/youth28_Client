package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.UserModify
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyViewModel: ViewModel() {
    val TAG = "ModifyViewModel"

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val profile = MutableLiveData<String>()
    var errMsg = MutableLiveData<String>()

    val cbStudy = MutableLiveData<Boolean>()
    val cbWork = MutableLiveData<Boolean>()
    val cbGame = MutableLiveData<Boolean>()
    val cbMusic = MutableLiveData<Boolean>()
    val cbArt = MutableLiveData<Boolean>()
    val cbExercise = MutableLiveData<Boolean>()
    val cbEtc = MutableLiveData<Boolean>()

    val onModifyImageEvent = SingleLiveEvent<Unit>()
    val onSaveEvent = SingleLiveEvent<Unit>()

    var strField = ""

    init {
        name.value = UserData.userName
        profile.value = UserData.userProfile

        Log.e(TAG, "${UserData.userName}, ${UserData.userId}, ${UserData.userPassword}")

        if (UserData.userField == "") {
            val arrayList = UserData.userField.split(",")
            for (value in arrayList) {
                when (value) {
                    "스터디" -> cbStudy.value = true
                    "게임" -> cbGame.value = true
                    "업무" -> cbWork.value = true
                    "음악" -> cbMusic.value = true
                    "미술" -> cbArt.value = true
                    "운동(헬스)" -> cbExercise.value = true
                    "기타" -> cbEtc.value = true
                }
            }
        }
    }

    fun onSave() {
        if (name.value == "") {
            errMsg.value = "이름을 입력해주세요"
        } else if (email.value == "" || email.value == null) {
            errMsg.value = "이메일을 입력해주세요"
        } else if (password.value == "" || password.value == null) {
            errMsg.value = "현재 비밀번호를 입력해주세요"
        } else if (email.value != "" && password.value != "") {
            if (UserData.userId != email.value) {
                errMsg.value = "일치하지 않는 아이디 입니다."
            }
            if (UserData.userPassword != password.value) {
                errMsg.value = "일지하지 않는 비밀번호 입니다."
            }

            if (UserData.userId == email.value && UserData.userPassword == password.value) {

                strField = ""
                if (cbStudy.value == true) strField += "스터디,"
                if (cbWork.value == true) strField += "업무,"
                if (cbGame.value == true) strField += "게임,"
                if (cbMusic.value == true) strField += "음악,"
                if (cbArt.value == true) strField += "미술,"
                if (cbExercise.value == true) strField += "운동(헬스),"
                if (cbEtc.value == true) strField += "기타,"

                if(strField.length >0) {
                    strField = strField.substring(0, strField.length - 1)
                }

                val user = UserModify(UserData.userNum.toInt(), name.value.toString(), profile.value.toString(), strField)
                Log.e(TAG, user.toString())
                val call = RetrofitHelper.getUserApi().modify(user)
                call.enqueue(object : Callback<UserModify> {
                    override fun onResponse(call: Call<UserModify>, response: Response<UserModify>) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                UserData.userName = name.value.toString()
                                UserData.userProfile = profile.value.toString()

                                errMsg.value = "수정되었습니다."

                                onSaveEvent.call()
                            }
                        } else Log.e(TAG, response.message())
                    }
                    override fun onFailure(call: Call<UserModify>, t: Throwable) {
                        Log.e(TAG, "통신안됨: ${t.message}")
                    }

                })
            }
        }
    }

    fun onModifyImage() {
        onModifyImageEvent.call()
    }
}