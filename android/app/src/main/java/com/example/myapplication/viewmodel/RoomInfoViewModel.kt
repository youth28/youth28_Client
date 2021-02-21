package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.RoomInfoDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoViewModel: ViewModel() {
    val TAG = "RoomInfoViewModel"

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

    val onUpdateRoomEvent = SingleLiveEvent<Unit>()

    var field = ""
    var room_id = 0
    var roomManager = 0
    var maxNum = 0

    init {
        room_id = RoomData.roomId

        title.value = "안녕"
        maxNum = 5
        maxPeo.value = "${maxNum}명"
        field = "스터디,게임,미술"
        profile.value = "http://d3c30c5e052c.ngrok.io/uploads/98eed633ca782547430c1768572e1cdb.png"
        roomManager = 5

        val arrayList = field.split(",")
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

    fun onUpdateRoom() {
        if (roomManager == UserData.userNum.toInt()) {
            onUpdateRoomEvent.call()
        } else {
            errMsg.value = "방에대한 수정권한이 없습니다."
            Log.e(TAG, "userId=${UserData.userNum}, roomManager=${roomManager}")
        }
    }

    fun settingUi() {
        room_id = RoomData.roomId

        cbStudy.value = false
        cbGame.value = false
        cbWork.value = false
        cbMusic.value = false
        cbArt.value = false
        cbExercise.value = false
        cbEtc.value = false


        title.value = RoomData.title
        maxNum = RoomData.maxPeo
        maxPeo.value = "${maxNum}명"
        field = RoomData.roomField
        profile.value = RoomData.profile
        roomManager = 5

        val arrayList = field.split(",")
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

        val room = RoomId(room_id)
        val call = RetrofitHelper.getRoomApi().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        field = response.body()!!.field
                        title.value = response.body()!!.title
                        profile.value = response.body()!!.profile
                        maxPeo.value = "${response.body()!!.maxPeo}명"
                        roomManager = response.body()!!.room_manager

                        Log.e(TAG, "room_manager: ${roomManager}")

                        // region checkBox 설정하기
                        field = field.substring(0, field.length - 1)
                        Log.e(TAG + "field", field)

                        val arrayList = field.split(",")
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
                        // endregion

                        Log.e(TAG, "정보 받아오기 완료")

                    }
                } else Log.e("$TAG ERR", response.message())
            }

            override fun onFailure(call: Call<RoomInfoDTO>, t: Throwable) {
                Log.e("$TAG Err", "통신안됨: ${t.message}")
            }

        })
    }

}