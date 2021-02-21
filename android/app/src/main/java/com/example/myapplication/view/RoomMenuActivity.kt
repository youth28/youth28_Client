package com.example.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRoomMenuBinding
import com.example.myapplication.viewmodel.RoomFindViewModel
import com.example.myapplication.viewmodel.RoomMenuViewModel
import kotlinx.android.synthetic.main.activity_room_menu.*

class RoomMenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoomMenuBinding
    private lateinit var viewmodel: RoomMenuViewModel

    var roomId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomId")) {
            roomId = intent.getIntExtra("roomId", 125)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_menu)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomMenuViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        with(viewmodel) {
            onVoteListEvent.observe(this@RoomMenuActivity, {
                val intent = Intent(this@RoomMenuActivity, VoteListActivity::class.java)
                startActivity(intent)
            })
            onRoomInfoEvent.observe(this@RoomMenuActivity, {
                val intent = Intent(this@RoomMenuActivity, RoomInfoActivity::class.java)
                intent.putExtra("roomId", roomId)
                startActivity(intent)
            })
        }
    }
}