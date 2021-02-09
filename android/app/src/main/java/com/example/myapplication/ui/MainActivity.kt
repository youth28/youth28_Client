package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.MyRoomsDTO
import com.example.myapplication.dto.UserId
import com.example.myapplication.MyRoom
import com.example.myapplication.R
import com.example.myapplication.adapter.RoomAdapter
import com.example.myapplication.RoomModel
import com.example.myapplication.adapter.MyRoomListAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    internal lateinit var preferences: SharedPreferences
    val list: ArrayList<RoomModel> = arrayListOf()
    val myRoomList: ArrayList<MyRoom> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.main = this@MainActivity

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        // region 사이트 메뉴 바 ListView

        val user_id = UserId(preferences.getString("userNum", "0")!!.toInt())

        roomRcv()
        myRoomListView()

        for(i: Int in 1..5) {
            myRoomList.add(MyRoom("방이름 $i", i))
        }

        myRoomRcv()

        Log.e("user_id", user_id.toString())
        // endregion

        // region 사이드 메뉴바 요소
        layout_myPage.setOnClickListener {
            val intent = Intent(this@MainActivity, MyPageActivity::class.java)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
            Log.e("log", "logOut")
        }
        // endregion

        val call2 = RetrofitHelper.getApiService().room_list(user_id)
        call2.enqueue(object : Callback<MyRoomsDTO>{
            override fun onResponse(call: Call<MyRoomsDTO>, response: Response<MyRoomsDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()
//                    val array = arrayListOf<Int>()
//                    for (i: Int in 1..result!!.room.size) {
//
//                    }

                    for (i: Int in 1..result!!.room.size) {
                        val field = result.room[i-1].field.substring(0, result.room[i-1].field.length -1 )
                        val fieldArray = field.split(",")
                        val info = result.room[i-1]

                        list.add(RoomModel(info.room_id, info.title, info.maxPeo,
                                            fieldArray, info.profile))
                        Log.e(TAG, "RoomModel(room_id=${info.room_id}, title='${info.title}', maxPeo=${info.maxPeo}," +
                                " field='$fieldArray', profile='${info.profile}')")

                        roomRcv()
                    }
                } else {
                    Log.e(TAG, "메인 리스트: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG, "통신 안됨: ${t.message}")
            }

        })

        binding.btnMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
            myRoomListView()
        }

        binding.btnSearch.setOnClickListener {
            val intent = Intent(this@MainActivity, RoomFindActivity::class.java)
            startActivity(intent)
        }

        binding.fabRoomMake.setOnClickListener {
            val intent = Intent(this@MainActivity, RoomMakeActivity::class.java)
            startActivity(intent)
        }
    }

    fun roomRcv() {
        val mAdapter = RoomAdapter(this)
        binding.rcvRoomList.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        binding.rcvRoomList.layoutManager = layoutManager
        binding.rcvRoomList.setHasFixedSize(true)
        for(i: Int in 1..5) {
            list.add(RoomModel(i, "title${i}", i + 1, arrayListOf("안녕", "하이"), "http://d24a94107e01.ngrok.io/uploads/359cc2d83bd7eecabec16e64a2690efd.jpg"))
        }
        mAdapter.list = list
    }
    
    fun myRoomRcv() {
        val myRoomAdapter = MyRoomListAdapter(this@MainActivity)
        rcvMyRoomList.adapter = myRoomAdapter
        val layoutManager = LinearLayoutManager(this@MainActivity)
        rcvMyRoomList.layoutManager = layoutManager
        rcvMyRoomList.setHasFixedSize(true)
        myRoomAdapter.list = myRoomList
    }

    fun myRoomListView() {

        val user_id = UserId(preferences.getString("userNum", "0")!!.toInt())
        val call = RetrofitHelper.getApiService().my_room(user_id = user_id)
        call.enqueue(object : Callback<MyRoomsDTO> {
            override fun onResponse(call: Call<MyRoomsDTO>, response: Response<MyRoomsDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    myRoomList.clear()
                    for (i: Int in 1..result!!.room.size) {
                        myRoomList.add(MyRoom(result.room[i - 1].title, result.room[i - 1].room_id))
                        Log.e(TAG, myRoomList[i-1].toString())
                        myRoomRcv()
                    }
                    
                } else {
                    Log.e(TAG, "사이드 리스트: ${response.message()}")
                }

            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG + "ERR", "통신 안됨: ${t.message}")
            }

        })
        Log.e(TAG, "myRoomListView")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}