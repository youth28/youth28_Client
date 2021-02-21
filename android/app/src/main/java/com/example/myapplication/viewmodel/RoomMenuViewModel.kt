package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.event.SingleLiveEvent

class RoomMenuViewModel: ViewModel() {
    val TAG = "RoomMenuViewModel"

    val onVoteListEvent = SingleLiveEvent<Unit>()
    val onRoomInfoEvent = SingleLiveEvent<Unit>()

    fun onVoteList() {
        onVoteListEvent.call()
    }

    fun onRoomInfo() {
        onRoomInfoEvent.call()
    }
}