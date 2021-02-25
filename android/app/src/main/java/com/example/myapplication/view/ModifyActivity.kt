package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.databinding.ActivityModifyBinding
import com.example.myapplication.viewmodel.ModifyViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_modify.*
import kotlinx.android.synthetic.main.activity_room_modify.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import java.io.*

class ModifyActivity: AppCompatActivity() {

    val TAG = "ModifyActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivityModifyBinding
    private lateinit var viewModel : ModifyViewModel

    var user_id: Int = 0
    var name = ""
    var PW = ""
    var email = ""
    var profile = "http://d3c30c5e052c.ngrok.io/uploads/798545ff1676d64dd4faea6b36bb0f94.png"
    var field = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userName = preferences.getString("userName", "없음").toString()
        UserData.userId = "dkstnqls"
        UserData.userPassword = "asdf1234"

        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ModifyViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        getImage()

        with(viewModel) {
            onModifyImageEvent.observe(this@ModifyActivity, {
                val intent = Intent(this@ModifyActivity, ModifyProfileActivity::class.java)
                intent.putExtra("mode", "2")
                startActivity(intent)
            })
            onSaveEvent.observe(this@ModifyActivity, {
                finish()
            })
        }

        viewModel.errMsg.observe(this, {
            showToast(it)
        })
    }

    fun getImage() {
        Picasso.get().load("http://d3c30c5e052c.ngrok.io/uploads/798545ff1676d64dd4faea6b36bb0f94.png").into(binding.imgProfile)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}