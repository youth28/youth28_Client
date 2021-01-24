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
import com.example.myapplication.MyRoom
import com.example.myapplication.R
import com.example.myapplication.RoomModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*

class MainActivity: AppCompatActivity() {

    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        // region 사이트 메뉴 바 ListView
        var myRoomList = arrayListOf<MyRoom>()
        for (i: Int in 1..5){
            myRoomList.add(MyRoom("이건 방이다 ${i}", i))
        }
        val myRoomAdapter = MyRoomListAdapter(this, myRoomList)
        listView.adapter = myRoomAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            showToast("roomName: ${myRoomList[position].roomName}, roomId: ${myRoomList[position].roomId}")
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

        val data = arrayListOf<Map<RoomModel, ArrayList<String>>>(

        )

        btnMenu.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
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