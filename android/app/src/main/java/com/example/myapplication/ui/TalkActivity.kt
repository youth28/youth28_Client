package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.ChatModel
import com.example.myapplication.DTO.RoomId
import com.example.myapplication.DTO.RoomInfoDTO
import com.example.myapplication.DTO.RoomMakeDTO
import com.example.myapplication.R
import com.example.myapplication.dialog.VoteDialog
import com.google.android.material.transition.Hold
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_room_info.*
import kotlinx.android.synthetic.main.activity_talk.*
import kotlinx.android.synthetic.main.item_my_chat.view.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TalkActivity : AppCompatActivity() {

    val TAG = "TalkActivity"

    val msgList = mutableListOf<ChatModel>()

    internal lateinit var preferences: SharedPreferences

    var title = ""
    var maxPro = 0
    var field = ""
    var profile = ""
    var room_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)

        if (intent.hasExtra("roomName")) {
            title = intent.getStringExtra("roomName").toString()
            room_id = intent.getIntExtra("roomId", 0)
        }

        settingUi()

        // recyclerView setting
        val layouManager = LinearLayoutManager(this@TalkActivity)
        layouManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewChat.layoutManager = layouManager
        recyclerViewChat.adapter = ChatAdapter()

        btnPlus.setOnClickListener {
            val intent = Intent(this@TalkActivity, RoomMenuActivity::class.java)
            intent.putExtra("roomId", room_id)
            startActivity(intent)
        }

        imgCalendar.setOnClickListener {
            val intent = Intent(this@TalkActivity, CalendarActivity::class.java)
            startActivity(intent)
        }
    }

    fun settingUi () {
        val room = RoomId(room_id)

        val arrayList = field.split(",")

        val layouManager = LinearLayoutManager(this@TalkActivity)
        layouManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewTagR.layoutManager = layouManager
        recyclerViewTagR.adapter = MyAdapter(arrayList)
        val call = RetrofitHelper.getApiService().room_info(room)
        call.enqueue(object : Callback<RoomInfoDTO> {
            override fun onResponse(call: Call<RoomInfoDTO>, response: Response<RoomInfoDTO>) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        Log.e(TAG, response.body().toString())
                        field = response.body()!!.field
                        title = response.body()!!.title
                        profile = response.body()!!.profile
                        maxPro = response.body()!!.maxPeo

                        tvRoomName.text = title

                        // region checkBox 설정하기
                        field = field.substring(0, field.length -1 )
                        Log.e(TAG+ "field", field)

                        val arrayList = field.split(",")

                        val layouManager = LinearLayoutManager(this@TalkActivity)
                        layouManager.orientation = LinearLayoutManager.HORIZONTAL
                        recyclerViewTagR.layoutManager = layouManager
                        recyclerViewTagR.adapter = MyAdapter(arrayList)
                        // endregion

                    }
                }
            }

            override fun onFailure(call: Call<RoomInfoDTO>, t: Throwable) {
                Log.e(TAG+" Err", "통신안됨: ${t.message}")
            }

        })
    }

    override fun onResume() {
        super.onResume()

        settingUi()
    }

    inner class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View

            // viewType 이 1이면 내 채팅 레이아웃, 2이면 상대 채팅 레이아웃
            // 3이면 내 투포 레이아웃, 4이면 상대 투표 레이아웃
            if(viewType == 1) {
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_my_chat, parent, false)
                return Holder(view)
            } else if (viewType == 2) {
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_your_chat, parent, false)
                return Holder2(view)
            } else if (viewType ==3){
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_my_vote, parent, false)
                return Holder3(view)
            } else {
                view = LayoutInflater.from(this@TalkActivity).inflate(R.layout.item_your_vote, parent, false)
                return Holder4(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is Holder) {
                (holder as Holder).chatText?.text = msgList.get(position).message
                (holder as Holder).chatDate?.text = msgList.get(position).time
            }
            else if (holder is Holder2) {
                (holder as Holder2).chatText?.text = msgList.get(position).message
                (holder as Holder2).chatYouImage?.setImageResource(R.drawable.logo)
                (holder as Holder2).chatYouName?.text = msgList.get(position).userId
                (holder as Holder2).chatDate?.text = msgList.get(position).time
            }
            else if (holder is Holder3) {
                (holder as Holder3).voteDate?.text = ""
                (holder as Holder3).voteTitle?.text = ""
                (holder as Holder3).voteAgreeUser?.text = ""
                (holder as Holder3).btnVote?.setOnClickListener {
                    // 다이얼로그 보이기
                    imgProfile.setOnClickListener {
                        val dialog = VoteDialog()

                        dialog.title = "오늘 뭐하지?"
                        dialog.date = "2021-02-10"
                        dialog.writer = "작성자: 누구누구"
                        dialog.listener = {checkRB ->

                        }
                        dialog.show(supportFragmentManager, "dialog")
                    }
                }
            }
            else if (holder is Holder4) {
                (holder as Holder4).voteDate?.text = ""
                (holder as Holder4).voteTitle?.text = ""
                (holder as Holder4).voteAgreeUser?.text = ""
            }
        }

        override fun getItemCount(): Int {
            return msgList.size
        }

        override fun getItemViewType(position: Int): Int {
            preferences = getSharedPreferences("user", Context.MODE_PRIVATE)

            Log.e(TAG, msgList.get(position).userId)

            // 내 아이디와 msgList의 name이 같다면 내 뷰타입 아니면 상대 뷰타입
            // 여기에 vote를 줘야하는디 이게 chat 모드랑 vote 모드로 나눠저야할 듯 내일 회의에서 말하자아ㅏ
            return if (msgList.get(position).userId == preferences.getString("userId", "")) {
                1
            } else {
                2
            }
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

    inner class MyAdapter(val tagList: List<String>) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@TalkActivity).inflate(R.layout.row_tag, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val tag = tagList[position]
            tag?.let {
                holder.tvTagName.text = "#${tag}"
            }
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTagName = itemView.findViewById<TextView>(R.id.tvTagName)
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}