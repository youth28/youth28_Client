package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ChatModel
import com.example.myapplication.R
import com.example.myapplication.UserData
import com.example.myapplication.adapter.ChatAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityTalkBinding
import com.example.myapplication.viewmodel.TalkViewModel
import kotlinx.android.synthetic.main.activity_talk.*
import org.json.JSONObject
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TalkActivity : AppCompatActivity() {
    val TAG = "TalkActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityTalkBinding
    private lateinit var viewModel: TalkViewModel

    private var hasConnection: Boolean = false

    //private var mSocket: Socket = IO.socket("[your server url]")
    private var mSocket: Socket = IO.socket("http://6cbca33abadc.ngrok.io/")

    var title = ""
    var room_id = 0

    val tagList = MutableLiveData<ArrayList<String>>()
    val chatList = MutableLiveData<ArrayList<ChatModel>>()

    val mAdapter = ChatAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomName")) {
            title = intent.getStringExtra("roomName").toString()
            room_id = intent.getIntExtra("roomId", 0)
        }

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userId = preferences.getString("userId", "dkstnqls").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(TalkViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()

        viewModel.room_id = room_id
        viewModel.title.value = title

        viewModel.tagList.observe(this, { livedata ->
            tagList.value = livedata
            val mAdapter = TagAdapter(this)
            val layouManager = LinearLayoutManager(this)
            layouManager.orientation = LinearLayoutManager.HORIZONTAL
            rcvTag.layoutManager = layouManager
            rcvTag.adapter = mAdapter
            mAdapter.list = tagList.value!!
        })

        viewModel.chatList.observe(this, { livedata ->
            chatList.value = livedata
            val layouManager = LinearLayoutManager(this)
            rcvChat.layoutManager = layouManager
            rcvChat.adapter = mAdapter
            mAdapter.chatList = chatList.value!!
            Log.e(TAG, "livedata= ${livedata}")
        })

        with(viewModel) {
            onPlusEvent.observe(this@TalkActivity, {
                val intent = Intent(this@TalkActivity, RoomMenuActivity::class.java)
                intent.putExtra("roomId", room_id)
                startActivity(intent)
            })
            onCalendarEvent.observe(this@TalkActivity, {
                val intent = Intent(this@TalkActivity, RoomScheduleActivity::class.java)
                intent.putExtra("roomId", room_id)
                startActivity(intent)
            })
        }

        if (savedInstanceState != null) {
            hasConnection = savedInstanceState.getBoolean("hasConnection")
        }

        if (hasConnection) {

        } else {
            //소켓연결
            mSocket.connect()


            //서버에 신호 보내는거같음 밑에 에밋 리스너들 실행
            //socket.on은 수신
            mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)

            val userId = JSONObject()
            try {
                userId.put("username", "안수빈" + " Connected")
                userId.put("roomName", "room예시")
                Log.e("username", "안수빈" + " Connected")

                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        hasConnection = true

        btnSend.setOnClickListener {
            //아이템 추가 부분
            sendMessage()

        }
    }

    internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val script: String
            val profile_image: String
            val date_time: String
            try {
                Log.e("asdasd", data.toString())
                name = data.getString("name")
                script = data.getString("script")
                profile_image = data.getString("profile_image")
                date_time = data.getString("date_time")


                val format = ChatModel(script, date_time, name, profile_image)
                viewModel.addItem(format)
                Log.e("new me",name )
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    //어플 키자마자 서버에  connect user 하고 프로젝트에 on new user 실행
    internal var onNewUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val length = args.size

            if (length == 0) {
                return@Runnable
            }
            //Here i'm getting weird error..................///////run :1 and run: 0
            var username = args[0].toString()
            try {
                val `object` = JSONObject(username)
                username = `object`.getString("username")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        })
    }

    fun sendMessage() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        //나중에 바꿔줄것
        val sdf = SimpleDateFormat("yyyy-MM-dd")

        val getTime = sdf.format(date)

        val message = viewModel.msg.value.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(message)) {
            return
        }
        viewModel.msg.value = ""
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", "안수빈")


            jsonObject.put("script", message)
            jsonObject.put("profile_image", "example")
            jsonObject.put("date_time", getTime)
            jsonObject.put("roomName", "room예시")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.e("챗룸", "sendMessage: 1" + mSocket.emit("chat message", jsonObject))
        Log.e("sendmmm","안수빈" )
    }

    fun loadMessage(){
        /*preferences = getSharedPreferences("auto", MODE_PRIVATE)
        val editor = preferences.edit()
        var room : ChatModel = ChatModel(preferences.getString("inputId", "").toString(), preferences.getString("second_email","").toString())
        var call: Call<LoadMsgDTO>? = RetrofitHelper.getApiService().chat_load(room)
        call?.enqueue(object : Callback<LoadMsgDTO> {
            override fun onResponse(call: Call<LoadMsgDTO>, response: Response<LoadMsgDTO>) {
                Log.e("성공입니당~",response.body().toString())
                var result : LoadMsgDTO? = response.body()
                var name : String
                var msg : String
                var email : String
                var date_time : String

                editor.putString("roomName", result!!.room)
                editor.apply()

                Log.e("result", preferences.getString("roomName", "0")!!)
                for (i in 0.. result!!.count-1){
                    name = result.chatLine.get(i).name
                    msg = result.chatLine.get(i).msg
                    date_time = result.chatLine.get(i).date
                    email = preferences.getString("second_email","").toString()

                    val format = ChatModel(name, msg, "profileImage", date_time, email, "없음")
                    mAdapter.addItem(format)
                    mAdapter.notifyDataSetChanged()
                    // 메세지가 올라올때마다 스크롤 최하단으로 보내기
                    chat_recyclerview.scrollToPosition(((chat_recyclerview.adapter?.itemCount ?: Int) as Int) - 1)
                }
                val userId = JSONObject()
                try {
                    userId.put("room", preferences.getString("roomName", "0"))
                    roomNumber = preferences.getString("roomName", "")!!
                    Log.e("username",preferences.getString("inputId", "") + " Connected")

                    //socket.emit은 메세지 전송임
                    mSocket.emit("connect user", userId)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<LoadMsgDTO>, t: Throwable) {
                Log.e("d실패", t.message.toString())
            }

        })*/
    }
}