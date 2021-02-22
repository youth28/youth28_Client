package com.example.myapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.dto.VoteDTO

class VoteListViewModel: ViewModel() {
    val TAG = "VoteListViewModel"

    val voteList = MutableLiveData<ArrayList<VoteDTO>>()

    init {
        val data = arrayListOf<VoteDTO>()
        for(i in 1..5){
            data.add(VoteDTO("투표 제목$i", "2021-01-$i"))
        }
        voteList.postValue(data)
    }
}