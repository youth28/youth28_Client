package com.example.myapplication.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ChatModel
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.databinding.ItemMyChatBinding
import com.example.myapplication.databinding.ItemYourChatBinding
import com.example.myapplication.dto.UserId
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "ChatAdapter"

    var chatList = mutableListOf<ChatModel>()
    var curNum = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // viewType 이 1이면 내 채팅 레이아웃, 2이면 상대 채팅 레이아웃
        if(viewType == 1) {
            val binding = ItemMyChatBinding.inflate(LayoutInflater.from(context), parent, false)
            return Holder(binding)
        } else {
            val binding = ItemYourChatBinding.inflate(LayoutInflater.from(context), parent, false)
            return Holder2(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.onBind(item = chatList[position])
        }
        else if (holder is Holder2) {
            holder.onBind(item = chatList[position])
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getItemViewType(position: Int): Int {
        curNum = position

        // 내 아이디와 msgList의 name이 같다면 내 뷰타입 아니면 상대 뷰타입
        // 여기에 vote를 줘야하는디 이게 chat 모드랑 vote 모드로 나눠저야할 듯 내일 회의에서 말하자아ㅏ
        return if (chatList[position].user_id == UserData.userNum) {
            1
        } else {
            2
        }
    }

    // 내 채팅 뷰홀더
    inner class Holder(val binding: ItemMyChatBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ChatModel) {
            binding.itme = item
        }
    }

    // 상대 채팅 뷰홀더
    inner class Holder2(val binding: ItemYourChatBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ChatModel) {
            binding.item = item
            imageLoad(binding.imgChatYou)
        }
    }

    fun imageLoad(img: CircleImageView) {
        Log.e(TAG, "imageLoad")

        val call = RetrofitHelper.getImageApi().imageLoad(UserId(chatList[curNum].user_id.toInt()))
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

    // 내 투표 뷰 홀더
    inner class Holder3(itemView: View): RecyclerView.ViewHolder(itemView){
        val voteDate = itemView?.findViewById<TextView>(R.id.tvVoteDate)
        val voteTitle = itemView?.findViewById<TextView>(R.id.tvVoteTitle)
        val voteAgreeUser = itemView?.findViewById<TextView>(R.id.tvAgreeUser)
        val btnVote = itemView?.findViewById<Button>(R.id.btnVote)
    }

    inner class Holder4(itemView: View): RecyclerView.ViewHolder(itemView) {
        val voteDate = itemView?.findViewById<TextView>(R.id.tvVoteDate)
        val voteTitle = itemView?.findViewById<TextView>(R.id.tvVoteTitle)
        val voteAgreeUser = itemView?.findViewById<TextView>(R.id.tvAgreeUser)
    }
}