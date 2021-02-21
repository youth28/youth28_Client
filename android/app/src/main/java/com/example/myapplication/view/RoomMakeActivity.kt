package com.example.myapplication.view

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomMakeDTO
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.ActivityRoomMakeBinding
import com.example.myapplication.viewmodel.RoomMakeViewModel
import kotlinx.android.synthetic.main.activity_room_make.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomMakeActivity: AppCompatActivity() {

    val TAG = "RoomMakeActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivityRoomMakeBinding
    private lateinit var viewmodel: RoomMakeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userNum = preferences.getString("userNum", "105").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_make)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomMakeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        with(viewmodel) {
            onRoomMakeEvent.observe(this@RoomMakeActivity, {
                finish()
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