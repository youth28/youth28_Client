package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.RoomData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.room.RoomModifyDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomModifyViewModel: ViewModel() {
    val TAG = "RoomModifyViewModel"

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

    val onSaveEvent = SingleLiveEvent<Unit>()

    var strField = ""
    var maxNum = 0

    init {
        setting()
    }

    fun setting() {
        Log.e(TAG, "roomField=${RoomData.roomField}")
        val arrayList = RoomData.roomField.split(",")
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
        title.value = RoomData.title
        maxNum = RoomData.maxPeo
        maxPeo.value = "${RoomData.maxPeo}"
    }

    fun onSaveRoom() {
        if (title.value == null || title.value == "") errMsg.value = "방 이름을 입력해주세요"
        else if (maxNum < 2) errMsg.value = "방 최소 인원은 2명입니다."
        else {
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

            if(strField.isNotEmpty()) {
                strField = strField.substring(0, strField.length - 1)
            }

            RoomData.title = title.value.toString()
            RoomData.maxPeo = maxPeo.value!!.toInt()
            RoomData.roomField = strField
            onSaveEvent.call()

            val room = RoomModifyDTO(RoomData.roomId, RoomData.title, RoomData.maxPeo, RoomData.roomField, )
            Log.e(TAG, room.toString())
            val call = RetrofitHelper.getRoomApi().modify_room(room)
            call.enqueue(object : Callback<RoomModifyDTO> {
                override fun onResponse(call: Call<RoomModifyDTO>, response: Response<RoomModifyDTO>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            errMsg.value = "성공적으로 방을 수정하였습니다."
                            RoomData.title = title.value.toString()
                            RoomData.maxPeo = maxPeo.value!!.toInt()
                            RoomData.roomField = strField
                            onSaveEvent.call()
                        }
                    } else Log.e(TAG, "통신안됨 ${response.message()}")
                }

                override fun onFailure(call: Call<RoomModifyDTO>, t: Throwable) {
                    Log.e(TAG, "통신안됨 ${t.message}")
                }

            })
        }
    }

    fun onPlu() {
        maxPeo.value = "${++maxNum}"
    }

    fun onSub() {
        maxPeo.value = "${--maxNum}"
    }
}