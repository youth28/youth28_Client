package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.ActivityModifyBinding
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.viewmodel.ModifyViewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_modify.*
import kotlinx.android.synthetic.main.activity_room_modify.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ModifyActivity: AppCompatActivity() {

    val TAG = "ModifyActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding : ActivityModifyBinding
    private lateinit var viewModel : ModifyViewModel

    var user_id: Int = 0
    var name = ""
    var email = ""
    var field = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userName = preferences.getString("userName", "없음").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ModifyViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        imageLoad(binding.imgProfile)

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

    fun imageLoad(img: CircleImageView) {
        img.setImageResource(R.drawable.add)

        val call = RetrofitHelper.getImageApi().imageLoad(UserId(UserData.userNum.toInt()))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("AAA", "REQUEST SUCCESS ==> ")
                    val file = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(file)
                    img.setImageBitmap(bitmap)
                } else {
                    Log.d("AAA", "통신오류=${response.message()}")
                    Picasso.get().load(R.drawable.add).into(img)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
            }

        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}