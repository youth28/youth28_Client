package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomMakeDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomMakeViewModel: ViewModel() {
    val TAG = "RoomModelViewModel"

    val title = MutableLiveData<String>()
    val maxPeo = MutableLiveData<String>()
    val profile = MutableLiveData<String>()
    val errMsg = MutableLiveData<String>()

    val cbStudy = MutableLiveData<Boolean>()
    val cbWork = MutableLiveData<Boolean>()
    val cbGame = MutableLiveData<Boolean>()
    val cbMusic = MutableLiveData<Boolean>()
    val cbArt = MutableLiveData<Boolean>()
    val cbExercise = MutableLiveData<Boolean>()
    val cbEtc = MutableLiveData<Boolean>()

    val onRoomMakeEvent = SingleLiveEvent<Unit>()

    var roomId = 0
    var strField = ""
    var maxNum = 1

    init {
        maxPeo.value = "1"
    }

    fun onCreateRoom() {
        if (title.value == null || title.value == "") errMsg.value = "방 이름을 입력해주세요"
        else if (maxNum < 2) errMsg.value = "방 최소 인원은 2명입니다."
        else {
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

            Log.e(TAG, "checkBox: $strField 선택됨")

            val room = RoomMakeDTO(title.value.toString(), maxNum, strField, profile.value.toString(), UserData.userNum.toInt())
            Log.e("roomMake", room.toString())
            onRoomMakeEvent.call()
            val call = RetrofitHelper.getRoomApi().make_room(room)
            call.enqueue(object : Callback<RoomMakeDTO> {
                override fun onResponse(call: Call<RoomMakeDTO>, response: Response<RoomMakeDTO>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            errMsg.value = "성공적으로 방을 만들었습니다. 사이드 메뉴에서 확인해주세요"
                            onRoomMakeEvent.call()
                        }
                    } else Log.e(TAG, "RoomMake: ${response.message()}")
                }

                override fun onFailure(call: Call<RoomMakeDTO>, t: Throwable) {
                    Log.e(TAG, "통신안됨 ${t.message}")
                }

            })
        }
    }

    fun onPlu() {
        if (maxNum < 20) {
            maxPeo.value = "${++maxNum}"
        }
    }

    fun onSub() {
        if (maxNum > 0) {
            maxPeo.value = "${--maxNum}"
        }
    }
}