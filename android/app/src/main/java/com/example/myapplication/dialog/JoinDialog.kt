package com.example.myapplication.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myapplication.API.RetrofitHelper
import com.example.myapplication.DTO.JoinRoomDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.dialog_join.view.*
import kotlinx.android.synthetic.main.dialog_vote.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinDialog: DialogFragment() {

    var mMainMsg : String? = null
    internal lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mMainMsg = arguments!!.getString(ARG_DIALOG_MAIN_MSG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder : AlertDialog.Builder = AlertDialog.Builder(activity)

        val view: View = activity!!.layoutInflater.inflate(R.layout.dialog_join, null)
        view.tvJoinDialog.text = mMainMsg
        view.btnYes.setOnClickListener {
            preferences = activity!!.getSharedPreferences("user", Activity.MODE_PRIVATE)
            val join = JoinRoomDTO(preferences.getString("userNum", "0")!!.toInt(), room_id)
            val call = RetrofitHelper.getApiService().room_join(join)
            call.enqueue(object : Callback<JoinRoomDTO> {
                override fun onResponse(call: Call<JoinRoomDTO>, response: Response<JoinRoomDTO>) {
                    if (response.isSuccessful) {
                        Log.e("성공", response.message())
                    } else {
                        Log.e("실패", response.message())
                    }
                }

                override fun onFailure(call: Call<JoinRoomDTO>, t: Throwable) {
                    Log.e(TAG+"ERR", "통신안됨 ${t.message}")
                }

            })
            dismiss()
        }
        view.btnNo.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "CustomDialogFragment"
        const val ARG_DIALOG_MAIN_MSG = "dialog_main_msg"
        var room_id = 0

        fun newInstance(mainMsg: String, roomId: Int): JoinDialog {
            val bundle = Bundle()
            bundle.putString(ARG_DIALOG_MAIN_MSG, mainMsg)
            room_id = roomId
            val fragment = JoinDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

}