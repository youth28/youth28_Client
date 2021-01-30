package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_room_menu.*

class RoomMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_menu)

        linVoteList.setOnClickListener {
            val intent = Intent(this@RoomMenuActivity, VoteListActivity::class.java)
            startActivity(intent)
        }

        linRoomInfo.setOnClickListener {
            val intent = Intent(this@RoomMenuActivity, RoomInfoActivity::class.java)
            startActivity(intent)
        }
    }
}