package com.example.myapplication.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.dto.room.RoomInfoDTO
import com.example.myapplication.event.SingleLiveEvent
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoViewModel: ViewModel() {
    val TAG = "RoomInfoViewModel"

    val title = MutableLiveData<String>()
    val maxPeo = MutableLiveData<String>()
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
    var roomManager = 0
    var maxNum = 0

    init {
        settingUi()
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
        cbStudy.value = false
        cbGame.value = false
        cbWork.value = false
        cbMusic.value = false
        cbArt.value = false
        cbExercise.value = false
        cbEtc.value = false
        val room = RoomId(RoomData.roomId)
        val call = RetrofitHelper.getRoomApi().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        field = response.body()!!.field
                        title.value = response.body()!!.title
                        maxNum = response.body()!!.maxPeo
                        maxPeo.value = "${maxNum}명"
                        roomManager = response.body()!!.room_manager

                        Log.e(TAG, "room_manager: ${roomManager}")

                        // region checkBox 설정하기
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

                    }
                } else Log.e("$TAG ERR", response.message())
            }

            override fun onFailure(call: Call<RoomInfoDTO>, t: Throwable) {
                Log.e("$TAG Err", "통신안됨: ${t.message}")
            }

        })
    }
}