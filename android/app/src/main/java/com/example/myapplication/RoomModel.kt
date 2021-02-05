package com.example.myapplication

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.dialog.JoinDialog

data class RoomModel(
        val room_id : Int,
        val title: String,
        val maxPeo: Int,
        val field: List<String>,
        val profile: String
) {
    override fun toString(): String {
        return "RoomModel(room_id=$room_id, title='$title', maxPeo=$maxPeo, field='$field', profile='$profile')"
    }
}
