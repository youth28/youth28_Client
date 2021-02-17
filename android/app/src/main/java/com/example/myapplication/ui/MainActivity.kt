package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.MyRoomsDTO
import com.example.myapplication.dto.UserId
import com.example.myapplication.MyRoom
import com.example.myapplication.R
import com.example.myapplication.adapter.RoomAdapter
import com.example.myapplication.RoomModel
import com.example.myapplication.UserData
import com.example.myapplication.adapter.MyRoomListAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.viewmodel.LoginViewModel
import com.example.myapplication.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity: AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    internal lateinit var preferences: SharedPreferences
    val list = MutableLiveData<ArrayList<RoomModel>>()
    val myRoomList = MutableLiveData<ArrayList<MyRoom>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userId = preferences.getString("userNum", "55").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        val dataObserver: Observer<ArrayList<RoomModel>> =
                Observer { livedata ->
                    list.value = livedata
                    val mAdapter = RoomAdapter(this)
                    binding.rcvRoomList.adapter = mAdapter
                    val layoutManager = LinearLayoutManager(this)
                    binding.rcvRoomList.layoutManager = layoutManager
                    binding.rcvRoomList.setHasFixedSize(true)
                    mAdapter.list = list.value!!
                }

        viewModel.list.observe(this, dataObserver)

        val roomDataObserver: Observer<ArrayList<MyRoom>> =
                Observer { livedata ->
                    myRoomList.value = livedata
                    val myRoomAdapter = MyRoomListAdapter(this@MainActivity)
                    rcvMyRoomList.adapter = myRoomAdapter
                    val layoutManager = LinearLayoutManager(this@MainActivity)
                    rcvMyRoomList.layoutManager = layoutManager
                    rcvMyRoomList.setHasFixedSize(true)
                    myRoomAdapter.list = myRoomList.value!!
                }

        viewModel.myRoomList.observe(this, roomDataObserver)

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

        with(viewModel) {
            onMenuEvent.observe(this@MainActivity, {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            })
            onSearchEvent.observe(this@MainActivity, {
                val intent = Intent(this@MainActivity, RoomFindActivity::class.java)
                startActivity(intent)
            })
            onRoomMakeEvent.observe(this@MainActivity, {
                val intent = Intent(this@MainActivity, RoomMakeActivity::class.java)
                startActivity(intent)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        viewModel.rcvMyRoomList()
        viewModel.rcvRoomList()
    }
    
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}