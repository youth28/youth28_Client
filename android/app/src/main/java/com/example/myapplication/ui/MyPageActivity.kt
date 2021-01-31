package com.example.myapplication.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.UserId
import com.example.myapplication.DTO.UserInfoDTO
import com.example.myapplication.R
import com.example.myapplication.dialog.VoteDialog
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.dialog_vote.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    val TAG = "MyPageActivity"

    internal lateinit var preferences: SharedPreferences

    val tagList = mutableListOf<String>()

    var strName = ""
    var strEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)

        // recyclerView setting
        val layouManager = LinearLayoutManager(this@MyPageActivity)
        layouManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewTag.layoutManager = layouManager
        recyclerViewTag.adapter = MyAdapter()

        getUserInfo()

        btnModify.setOnClickListener {
            val intent = Intent(this@MyPageActivity, ModifyActivity::class.java)
            startActivity(intent)
        }

    }

    fun getUserInfo() {
        val user = UserId(preferences.getString("userNum", "0")!!.toInt())
        val call = RetrofitHelper.getApiService().user_info(user)
        call.enqueue(object : Callback<UserInfoDTO> {
            override fun onResponse(call: Call<UserInfoDTO>, response: Response<UserInfoDTO>) {
                if (response.isSuccessful) {
                    val field = response.body()!!.field
                    val arrField = field.split(",")
                    for (itme in arrField) {
                        tagList.add("#${itme}")
                    }

                } else Log.e(TAG+"Err", response.message())
            }

            override fun onFailure(call: Call<UserInfoDTO>, t: Throwable) {
                Log.e(TAG, "통신안됨: ${t.message}")
            }

        })

        tvName.text = preferences.getString("userName", "이름 없음")
        tvEmail.text = preferences.getString("userId", "Id 없음")
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.row_tag, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val tag = tagList[position]
            tag?.let {
                holder.tvTagName.text = tag
            }
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTagName = itemView.findViewById<TextView>(R.id.tvTagName)
    }
}