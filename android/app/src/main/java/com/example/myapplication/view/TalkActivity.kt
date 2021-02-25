package com.example.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ChatModel
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.UserData
import com.example.myapplication.adapter.ChatAdapter
import com.example.myapplication.adapter.TagAdapter
import com.example.myapplication.databinding.ActivityTalkBinding
import com.example.myapplication.dto.ChatListDTO
import com.example.myapplication.dto.RoomId
import com.example.myapplication.viewmodel.TalkViewModel
import kotlinx.android.synthetic.main.activity_talk.*
import org.json.JSONObject
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class TalkActivity : AppCompatActivity() {
    val TAG = "TalkActivity"

    internal lateinit var preferences: SharedPreferences

    private lateinit var binding: ActivityTalkBinding
    private lateinit var viewModel: TalkViewModel

    private var hasConnection: Boolean = false

    // flask
    //private var mSocket: Socket = IO.socket("http://f9cb158980e6.ngrok.io/")
    // node
    private var mSocket: Socket = IO.socket("http://db42a32178bf.ngrok.io")

    val tagList = MutableLiveData<ArrayList<String>>()
    val chatList = MutableLiveData<ArrayList<ChatModel>>()

    val mAdapter = ChatAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomName")) {
            RoomData.title = intent.getStringExtra("roomName").toString()
            RoomData.roomId = intent.getIntExtra("roomId", 0)
        }

        preferences = getSharedPreferences("user", Activity.MODE_PRIVATE)
        UserData.userId = preferences.getString("userId", "dkstnqls").toString()
        UserData.userNum = preferences.getString("userNum", "55").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_talk)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(TalkViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        binding.executePendingBindings()


        val layouManager = LinearLayoutManager(this)
        rcvChat.layoutManager = layouManager
        rcvChat.adapter = mAdapter

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
            mAdapter.chatList = chatList.value!!
            binding.rcvChat.adapter = mAdapter
            binding.rcvChat.scrollToPosition(binding.rcvChat.adapter!!.itemCount - 1)
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
            onSendEvent.observe(this@TalkActivity, {
                Log.e(TAG, viewModel.jsonObject.toString())
                Log.e("챗룸", "sendMessage: 1" + mSocket.emit("chat message", viewModel.jsonObject))
                Log.e("sendmmm", UserData.userId )
            })
        }

        // region 소캣
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
            mSocket.on("disconnect event", onDisconnect)

            val userId = JSONObject()
            try {
                userId.put("user_id", UserData.userNum)
                userId.put("room_id", RoomData.roomId)
                Log.e("send connect user", "user_id=${userId.getString("user_id")}, room_id=${userId.getString("room_id")}")

                //socket.emit은 메세지 전송임
                mSocket.emit("connect user", userId)
            } catch (e: JSONException) {
                Log.e(TAG, "하하 ${e.message}")
                e.printStackTrace()
            }

        }

        hasConnection = true
        // endregion
    }

    internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val script: String
            val profile_image: String
            val date_time: String
            val user_id: String
            try {
                Log.e("asdasd", data.toString())
                name = data.getString("user_name")
                user_id = data.getString("user_id")
                script = data.getString("message")
                profile_image = data.getString("profile_image")
                date_time = data.getString("date_time")


                val format = ChatModel(script, date_time, user_id, name, profile_image)
                viewModel.addItem(format)
                Log.e("new me",name )
            } catch (e: Exception) {
                Log.e("하하", e.message.toString())
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
                username = `object`.getString("user_id")
                Log.e("username onNuewUser", username)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        })
    }

    internal var onDisconnect: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            Log.e("하하", "onDisconnect")

            try {
                Log.e("disconnect", data.toString())
            } catch (e: Exception) {
                Log.e("하하", e.message.toString())
                e.printStackTrace()
            }
            return@Runnable
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("하하", "onDestroy")
        val user = JSONObject()
        user.put("user_id", UserData.userNum)
        user.put("room_id", RoomData.roomId)
        Log.e("user", user.toString())
        Log.e("send", " 제발!" + mSocket.emit("disconnect event", user))
    }
}