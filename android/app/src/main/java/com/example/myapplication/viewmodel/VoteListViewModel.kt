package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.RoomData
import com.example.myapplication.api.RetrofitHelper
import com.example.myapplication.dto.ItemVoteDTO
import com.example.myapplication.dto.RoomId
import com.example.myapplication.dto.VoteListDTO
import com.example.myapplication.event.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoteListViewModel: ViewModel() {
    val TAG = "VoteListViewModel"

    val voteList = MutableLiveData<ArrayList<ItemVoteDTO>>()

    val onCreateVoteEvent = SingleLiveEvent<Unit>()

    init {
        RoomData.roomId = 5

        onReadList()
    }

    fun onCreateVote() {
        onCreateVoteEvent.call()
    }

    fun onReadList() {

        val data = arrayListOf<ItemVoteDTO>()
        for(i in 1..5){
            if(i%2 == 0)
                data.add(ItemVoteDTO(i, "이것은 질문$i", "2021-02-1$i", 0, "나는 이름$i"))
            else
                data.add(ItemVoteDTO(i, "이것은 질문$i", "2021-02-1$i", 1, "나는 이름$i"))
        }
        voteList.postValue(data)

        val call6 = RetrofitHelper.getVoteApi().list_vote(RoomId(RoomData.roomId))
        call6.enqueue(object : Callback<VoteListDTO> {
            override fun onResponse(call: Call<VoteListDTO>, response: Response<VoteListDTO>) {
                if (response.isSuccessful) {
                    val result = response.body()!!.data
                    val data = arrayListOf<ItemVoteDTO>()
                    for (i: Int in 0 until result.size) {
                        data.add(ItemVoteDTO(result[i].question_id, result[i].question_text, result[i].time, result[i].done, result[i].name))
                    }
                    voteList.postValue(data)

                } else {
                    Log.e("list_vote", "${response.message()}")
                }
            }

            override fun onFailure(call: Call<VoteListDTO>, t: Throwable) {
                Log.e("list_vote_onFailure", "${t.message}")
            }

        })
    }
}