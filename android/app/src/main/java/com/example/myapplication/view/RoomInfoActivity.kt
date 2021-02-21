package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.databinding.ActivityRoomInfoBinding
import com.example.myapplication.viewmodel.RoomInfoViewModel
import kotlinx.android.synthetic.main.activity_room_info.*

class RoomInfoActivity : AppCompatActivity() {

    val TAG = "RoomInfoActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityRoomInfoBinding
    private lateinit var viewmodel: RoomInfoViewModel

    var room_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        room_id = intent.getIntExtra("roomId", 66)
        RoomData.roomId = room_id

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_info)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomInfoViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        with(viewmodel) {
            onUpdateRoomEvent.observe(this@RoomInfoActivity, {
                val intent = Intent(this@RoomInfoActivity, RoomModifyActivity::class.java)
                intent.putExtra("roomTitle", title.value)
                intent.putExtra("roomMax", maxPeo.value)
                intent.putExtra("roomField", field)
                intent.putExtra("roomId", room_id)
                intent.putExtra("roomProfile", profile.value)
                startActivity(intent)
            })
        }

        viewmodel.errMsg.observe(this, {
            showToast(it)
        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}