package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ChatModel
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.adapter.ChatAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityTalkBinding
import com.example.myapplication.viewmodel.TalkViewModel
import kotlinx.android.synthetic.main.activity_talk.*

class TalkActivity : AppCompatActivity() {
    val TAG = "TalkActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityTalkBinding
    private lateinit var viewModel: TalkViewModel

    var title = ""
    var room_id = 0

    val tagList = MutableLiveData<ArrayList<String>>()
    val chatList = MutableLiveData<ArrayList<ChatModel>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomName")) {
            title = intent.getStringExtra("roomName").toString()
            room_id = intent.getIntExtra("roomId", 0)
        }

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userId = preferences.getString("userId", "dkstnqls").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(TalkViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        viewModel.room_id = room_id
        viewModel.title.value = title

        viewModel.tagList.observe(this, { livedata ->
            tagList.value = livedata
            val mAdapter = TagAdapter(this)
            val layouManager = LinearLayoutManager(this)
            layouManager.orientation = LinearLayoutManager.HORIZONTAL
            rcvTag.layoutManager = layouManager
            rcvTag.adapter = mAdapter
            mAdapter.list = tagList.value!!
        })

        viewModel.chatList.observe(this, { livedata ->
            chatList.value = livedata
            val mAdapter = ChatAdapter(this)
            val layouManager = LinearLayoutManager(this)
            rcvChat.layoutManager = layouManager
            rcvChat.adapter = mAdapter
            mAdapter.chatList = chatList.value!!
        })

        with(viewModel) {
            onPlusEvent.observe(this@TalkActivity, {
                val intent = Intent(this@TalkActivity, RoomMenuActivity::class.java)
                intent.putExtra("roomId", room_id)
                startActivity(intent)
            })
            onCalendarEvent.observe(this@TalkActivity, {
                val intent = Intent(this@TalkActivity, RoomScheduleActivity::class.java)
                intent.putExtra("roomId", room_id)
                startActivity(intent)
            })
        }
    }
}