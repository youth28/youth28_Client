package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.myapplication.databinding.ActivityRoomInfoBinding
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.id.UserId
import com.example.myapplication.viewmodel.RoomInfoViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_room_info.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomInfoActivity : AppCompatActivity() {

    val TAG = "RoomInfoActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityRoomInfoBinding
    private lateinit var viewmodel: RoomInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        RoomData.roomId = intent.getIntExtra("roomId", 0)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_room_info)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(RoomInfoViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        imageLoad(imgProfile)

        with(viewmodel) {
            onUpdateRoomEvent.observe(this@RoomInfoActivity, {
                val intent = Intent(this@RoomInfoActivity, RoomModifyActivity::class.java)
                intent.putExtra("roomTitle", title.value)
                intent.putExtra("roomMax", maxNum)
                intent.putExtra("roomField", field)
                intent.putExtra("roomId", RoomData.roomId)
                startActivity(intent)
            })
        }

        viewmodel.errMsg.observe(this, {
            showToast(it)
        })
    }

    fun imageLoad(img: CircleImageView) {
        val call = RetrofitHelper.getImageApi().roomImageLoad(RoomId(RoomData.roomId))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val file = response.body()?.byteStream()
                    if(file != null) {
                        val bitmap = BitmapFactory.decodeStream(file)
                        img.setImageBitmap(bitmap)
                        RoomData.profile = true
                    } else img.setImageResource(R.drawable.add)
                } else {
                    Log.d("AAA", "통신오류=${response.message()}")
                    img.setImageResource(R.drawable.add)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("AAA", "FAIL REQUEST ==> " + t.message)
                img.setImageResource(R.drawable.add)
            }

        })
    }

    override fun onRestart() {
        super.onRestart()
        viewmodel.settingUi()
        imageLoad(imgProfile)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}