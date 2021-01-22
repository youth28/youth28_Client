package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        layout_myPage.setOnClickListener {
            Log.e("log", "gg")
            val intent = Intent(this@MainActivity, MyPageActivity::class.java)
            startActivity(intent)
        }

        btn_logout.setOnClickListener {
            Log.e("log", "logOut")
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }
}