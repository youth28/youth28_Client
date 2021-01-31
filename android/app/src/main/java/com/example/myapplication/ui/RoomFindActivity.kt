package com.example.myapplication.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.FieldDTO
import com.example.myapplication.DTO.RoomNameDTO
import com.example.myapplication.DTO.RoomsDTO
import com.example.myapplication.R
import com.example.myapplication.RoomAdapter
import com.example.myapplication.RoomModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_room_find.*
import kotlinx.android.synthetic.main.activity_room_find.btnSearch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomFindActivity : AppCompatActivity() {

    val TAG = "RoomFindActivity"

    var key = ""
    val list: ArrayList<RoomModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_find)

        listRecyclerView()

        val searchList: ArrayList<String> = arrayListOf(
                "스터디", "업무", "게임", "음악", "미술", "운동(헬스)", "기타"
        )

        val adapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                searchList
        )

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        autoTVRoomFind.setAdapter(adapter)

        autoTVRoomFind.threshold = 1

        autoTVRoomFind.setOnItemClickListener { parent, view, position, id ->
            showToast("{parent: $parent, view: $view, position: $position, id $id}")
        }

        btnSearch.setOnClickListener {
            key = autoTVRoomFind.text.toString()
            Log.e(TAG, key)

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
    }

    fun listRecyclerView() {
        val mAdapter = RoomAdapter(applicationContext, list)
        recyclerViewRoomFind.adapter = mAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewRoomFind.layoutManager = layoutManager
        recyclerViewRoomFind.setHasFixedSize(true)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

}