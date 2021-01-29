package com.example.myapplication.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.MyRoomsDTO
import com.example.myapplication.DTO.UserId
import com.example.myapplication.MyRoom
import com.example.myapplication.R
import com.example.myapplication.RoomAdapter
import com.example.myapplication.RoomModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity() {

    val TAG = "MainActivity"

    internal lateinit var preferences: SharedPreferences
    val list: ArrayList<RoomModel> = arrayListOf()
    var myRoomList = arrayListOf<MyRoom>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        // region 사이트 메뉴 바 ListView

        val user_id = UserId(preferences.getString("userNum", "0")!!.toInt())

        listRecyclerView()
        myRoomListView()

        Log.e("user_id", user_id.toString())
        val call = RetrofitHelper.getApiService().my_room(user_id = user_id)
        call.enqueue(object : Callback<MyRoomsDTO> {
            override fun onResponse(call: Call<MyRoomsDTO>, response: Response<MyRoomsDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    for (i: Int in 1..result!!.count) {
                        myRoomList.add(MyRoom(result.room[i - 1].title, result.room[i - 1].room_id))
                        myRoomListView()
                    }
                } else {
                    Log.e(TAG, "사이드 리스트: ${response.message()}")
                }

            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG + "ERR", "통신 안됨: ${t.message}")
            }

        })
//        for (i: Int in 1..5){
//            myRoomList.add(MyRoom("이건 방이다 ${i}", i))
//        }



        listView.setOnItemClickListener { parent, view, position, id ->
            showToast("roomName: ${myRoomList[position].roomName}, roomId: ${myRoomList[position].roomId}")
            val intent = Intent(this@MainActivity, TalkActivity::class.java)
            intent.putExtra("roomName", myRoomList[position].roomName)
            intent.putExtra("roomId", myRoomList[position].roomId)
            startActivity(intent)
        }
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
                    for (i: Int in 1..result!!.room.size) {
                        val field = result.room[i-1].field.substring(0, result.room[i-1].field.length -1 )
                        val fieldArray = field.split(",")
                        val info = result.room[i-1]
                        list.add(RoomModel(info.room_id, info.title, info.maxPeo,
                                            fieldArray, info.profile))
                        Log.e(TAG, "RoomModel(room_id=${info.room_id}, title='${info.title}', maxPeo=${info.maxPeo}," +
                                " field='$fieldArray', profile='${info.profile}')")

                        listRecyclerView()
                    }
                } else {
                    Log.e(TAG, "메인 리스트: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MyRoomsDTO>, t: Throwable) {
                Log.e(TAG, "통신 안됨: ${t.message}")
            }

        })

        btnMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
            myRoomListView()
        }

        btnSearch.setOnClickListener {
            val intent = Intent(this@MainActivity, RoomFindActivity::class.java)
            startActivity(intent)
        }

        fabRoomMake.setOnClickListener {
            val intent = Intent(this@MainActivity, RoomMakeActivity::class.java)
            startActivity(intent)
        }
    }

    fun listRecyclerView() {
        val mAdapter = RoomAdapter(this@MainActivity, list)
        recyclerViewRoomList.adapter = mAdapter
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerViewRoomList.layoutManager = layoutManager
        recyclerViewRoomList.setHasFixedSize(true)
    }

    fun myRoomListView() {
        val myRoomAdapter = MyRoomListAdapter(this@MainActivity, myRoomList)
        listView.adapter = myRoomAdapter
        myRoomAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    inner class MyRoomListAdapter (val context: Context, val myRoomList: ArrayList<MyRoom>) : BaseAdapter() {
        override fun getCount(): Int {
            return myRoomList.size
        }

        override fun getItem(position: Int): Any {
            return myRoomList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view : View = LayoutInflater.from(context).inflate(R.layout.my_room_list, null)
            val roomTitle = view.findViewById<TextView>(R.id.tvRoomName)

            val myRoom = myRoomList[position]
            roomTitle.text = myRoom.roomName

            return view
        }

    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}