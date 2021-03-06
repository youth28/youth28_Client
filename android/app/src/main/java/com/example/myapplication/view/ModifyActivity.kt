package com.example.myapplication.view

import android.content.Intent
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_modify.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyActivity: AppCompatActivity() {

    val TAG = "ModifyActivity"

    private lateinit var binding : ActivityModifyBinding
    private lateinit var viewModel : ModifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                if(UserData.userProfile) {
                    intent.putExtra("mode","2.5")
                } else {
                    intent.putExtra("mode", "2")
                }
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
        val call = RetrofitHelper.getImageApi().imageLoad(UserId(UserData.userNum.toInt()))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val file = response.body()?.byteStream()
                    if (file != null) {
                        val bitmap = BitmapFactory.decodeStream(file)
                        img.setImageBitmap(bitmap)
                    } else img.setImageResource(R.drawable.user_round)
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

    override fun onResume() {
        super.onResume()
        imageLoad(binding.imgProfile)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}