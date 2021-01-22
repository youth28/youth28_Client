package com.example.myapplication.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_room_find.*

class RoomFindActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_find)

        val searchList: ArrayList<String> = arrayListOf(
                "스터디", "업무", "게임", "음악", "미술", "운동(헬스)", "기타"
        )

        val adapter = ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                searchList
        )

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        autoTVRoomFind.setAdapter(adapter)

        autoTVRoomFind.threshold = 1

        autoTVRoomFind.setOnItemClickListener { parent, view, position, id ->
            showToast("{parent: $parent, view: $view, position: $position, id $id}")
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }

}