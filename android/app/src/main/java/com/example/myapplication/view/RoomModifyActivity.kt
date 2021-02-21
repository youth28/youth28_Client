package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.databinding.ActivityRoomModifyBinding
import com.example.myapplication.viewmodel.RoomModifyViewModel
import okhttp3.*
import java.io.*

class RoomModifyActivity : AppCompatActivity() {

    val TAG = "RoomModifyActivity"

    private lateinit var binding: ActivityRoomModifyBinding
    private lateinit var viewmodel: RoomModifyViewModel
    var room_id = 3
    var field = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomTitle")) {
            Log.e(TAG, "intent 있음")
            RoomData.roomField = intent.getStringExtra("roomField").toString()
            RoomData.roomId = intent.getIntExtra("roomId", 0)
            RoomData.maxPeo = intent.getIntExtra("roomMax", 10)
            RoomData.profile = intent.getStringExtra("roomProfile").toString()
            RoomData.title = intent.getStringExtra("roomTitle").toString()

            Log.e(TAG, "intent 설정")
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_modify)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomModifyViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        with(viewmodel) {
            onSaveEvent.observe(this@RoomModifyActivity, {
                val intent = Intent(this@RoomModifyActivity, ModifyProfileActivity::class.java)
                intent.putExtra("mode", "3")
                startActivity(intent)
                finish()
            })
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}