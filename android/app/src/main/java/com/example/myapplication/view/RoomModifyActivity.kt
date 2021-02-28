package com.example.myapplication.view

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.ActivityRoomModifyBinding
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.viewmodel.RoomModifyViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_room_modify.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            RoomData.roomField = intent.getStringExtra("roomField").toString()
            RoomData.roomId = intent.getIntExtra("roomId", 0)
            RoomData.maxPeo = intent.getIntExtra("roomMax", 10)
            RoomData.title = intent.getStringExtra("roomTitle").toString()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_modify)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomModifyViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        imageLoad(imgProfile)

        with(viewmodel) {
            onSaveEvent.observe(this@RoomModifyActivity, {
                val intent = Intent(this@RoomModifyActivity, ModifyProfileActivity::class.java)
                intent.putExtra("mode", "3")
                startActivity(intent)
                finish()
            })

            errMsg.observe(this@RoomModifyActivity, {
                showToast(it)
            })
        }
    }

    fun imageLoad(img: CircleImageView) {
        val call = RetrofitHelper.getImageApi().imageLoad(UserId(RoomData.roomId))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("AAA", "REQUEST SUCCESS ==> ")
                    val file = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(file)
                    img.setImageBitmap(bitmap)
                } else {
                    Log.d("AAA", "통신오류=${response.message()}")
                    img.setImageResource(R.drawable.add)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("AAA", "FAIL REQUEST ==> " + t.localizedMessage)
                img.setImageResource(R.drawable.add)
            }

        })
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}