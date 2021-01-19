package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.material.transition.Hold
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_talk.*
import kotlinx.android.synthetic.main.item_my_chat.view.*

class TalkActivity : AppCompatActivity() {

    val msgList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        // recyclerView setting
        val layouManager = LinearLayoutManager(this@TalkActivity)
        layouManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewChat.layoutManager = layouManager
        recyclerViewChat.adapter = ChatAdapter()
    }

    inner class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View

            // viewType 이 1이면 내 채팅 레이아웃, 2이면 상대 채팅 레이아웃
            if(viewType == 1) {
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_my_chat, parent, false)
                return Holder(view)
            } else {
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_your_chat, parent, false)
                return Holder2(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is Holder) {
                (holder as Holder).chatText?.text = msgList.get(position)
                (holder as Holder).chatDate?.text = msgList.get(position)
            }
            else if (holder is Holder2) {
                (holder as Holder2).chatText?.text = msgList.get(position)
                (holder as Holder2).chatYouImage?.setImageResource(R.drawable.logo)
                (holder as Holder2).chatYouName?.text = msgList.get(position)
                (holder as Holder2).chatDate?.text = msgList.get(position)
            }
        }

        override fun getItemCount(): Int {
            return msgList.size
        }

    }

    // 내 채팅 뷰홀더
    inner class Holder(itemView: View): RecyclerView.ViewHolder(itemView){
        val chatText = itemView?.findViewById<TextView>(R.id.tvMessage)
        val chatDate = itemView?.findViewById<TextView>(R.id.tvDate)
    }

    // 상대 채팅 뷰홀더
    inner class Holder2(itemView: View): RecyclerView.ViewHolder(itemView){
        val chatYouImage = itemView?.findViewById<ImageView>(R.id.imgChatYou)
        val chatYouName = itemView?.findViewById<TextView>(R.id.tvChatYouName)
        val chatText = itemView?.findViewById<TextView>(R.id.tvMessage)
        val chatDate = itemView?.findViewById<TextView>(R.id.tvDate)
    }
}