package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomNameDTO
import com.example.myapplication.dto.RoomsDTO
import com.example.myapplication.R
import com.example.myapplication.adapter.RoomAdapter
import com.example.myapplication.RoomModel
import com.example.myapplication.databinding.ActivityRoomFindBinding
import kotlinx.android.synthetic.main.activity_room_find.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomFindActivity : AppCompatActivity() {

    val TAG = "RoomFindActivity"

    private lateinit var binding: ActivityRoomFindBinding

    var key = ""
    val list: ArrayList<RoomModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_find)
        binding.activity = this

        listRecyclerView()
    }

    fun onFind(view: View) {
        list.clear()

        if (key == "") {
            showToast("방 제목을 입력해주세요.")
        } else {
            val roomName = RoomNameDTO(key)
            Log.e(TAG, roomName.toString())
            val call = RetrofitHelper.getApiService().room_search(roomName)
            call.enqueue(object : Callback<RoomsDTO> {
                override fun onResponse(call: Call<RoomsDTO>, response: Response<RoomsDTO>) {
                    if(response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                val result = response.body()
                                for (i: Int in 1..result!!.room.size) {
                                    val info = result.room[i-1]
                                    val field = info.field.substring(0, result.room[i-1].field.length -1)
                                    val fieldArray = field.split(",")
                                    list.add(RoomModel(info.room_id, info.title, info.maxPeo,
                                            fieldArray, info.profile))
                                    Log.e(TAG, "RoomModel(room_id=${info.room_id}, title='${info.title}', maxPeo=${info.maxPeo}," +
                                            " field='$fieldArray', profile='${info.profile}')")

                                    listRecyclerView()
                                }
                            }
                            204 -> {
                                showToast("해당 제목의 방이 존재하지 않습니다.")
                            }
                        }
                    } else Log.e(TAG+"ERR", "RoomFind통신: ${response.message()}")
                }

                override fun onFailure(call: Call<RoomsDTO>, t: Throwable) {
                    Log.e(TAG+"ERR", "통신안됨: ${t.message}")
                }

            })
        }
    }

    fun listRecyclerView() {
        val mAdapter = RoomAdapter(this)
        recyclerViewRoomFind.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerViewRoomFind.layoutManager = layoutManager
        recyclerViewRoomFind.setHasFixedSize(true)
        mAdapter.list = list
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

}