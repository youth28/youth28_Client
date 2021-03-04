package com.example.myapplication.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.room.RoomModel
import com.example.myapplication.databinding.RowRoomBinding
import com.example.myapplication.dialog.JoinDialog
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.id.UserId
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoomAdapter(val context: Context): RecyclerView.Adapter<RoomAdapter.Holder>() {
    val TAG = "RoomAdapter"

    var list = listOf<RoomModel>()

    var curNum = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowRoomBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list = list[position])

        curNum = position

        val mAdapter = TagAdapter(context)
        holder.binding.rcvTag.adapter = mAdapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        holder.binding.rcvTag.layoutManager = layoutManager
        holder.binding.rcvTag.setHasFixedSize(true)
        mAdapter.list = list[position].field

        holder.itemView.setOnClickListener {
            val dialog = JoinDialog(context)
            dialog.room_id = list[position].room_id
            dialog.title = "[${list[position].title}]"
            val manager = (context as AppCompatActivity).supportFragmentManager
            dialog.show(manager, "dialog")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: RowRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: RoomModel) {
            binding.room = list
            imageLoad(binding.imgRoomProfile)
        }
    }

    fun imageLoad(img: CircleImageView) {
        val call = RetrofitHelper.getImageApi().roomImageLoad(RoomId(list[curNum].room_id))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("AAA", "REQUEST SUCCESS ==> ")
                    val file = response.body()?.byteStream()
                    if (file != null) {
                        val bitmap = BitmapFactory.decodeStream(file)
                        img.setImageBitmap(bitmap)
                    } else img.setImageResource(R.drawable.add)

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



}