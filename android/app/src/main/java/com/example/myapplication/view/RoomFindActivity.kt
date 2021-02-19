package com.example.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.RoomNameDTO
import com.example.myapplication.dto.RoomsDTO
import com.example.myapplication.R
import com.example.myapplication.adapter.RoomAdapter
import com.example.myapplication.RoomModel
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityRoomFindBinding
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.RoomFindViewModel
import kotlinx.android.synthetic.main.activity_room_find.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomFindActivity : AppCompatActivity() {

    val TAG = "RoomFindActivity"

    private lateinit var binding: ActivityRoomFindBinding
    private lateinit var viewmodel: RoomFindViewModel

    var key = ""
    val list = MutableLiveData<ArrayList<RoomModel>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_find)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomFindViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        viewmodel.list.observe(this, { livedata ->
            list.value = livedata
            val mAdapter = RoomAdapter(this)
            recyclerViewRoomFind.adapter = mAdapter
            val layoutManager = LinearLayoutManager(this)
            recyclerViewRoomFind.layoutManager = layoutManager
            recyclerViewRoomFind.setHasFixedSize(true)
            mAdapter.list = list.value!!
        })

        viewmodel.errMsg.observe(this, {
            showToast(it)
        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

}