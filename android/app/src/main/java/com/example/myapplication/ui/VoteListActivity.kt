package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.dto.VoteDTO
import com.example.myapplication.R
import com.example.myapplication.adapter.VoteListAdapter
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_vote_list.*
import kotlinx.android.synthetic.main.row_vote.*

class VoteListActivity : AppCompatActivity() {

    val TAG = "VoteListActivity"

    val voteList = mutableListOf<VoteDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_list)

        createVote()
    }

    fun createVote() {
        // recyclerView setting
        val layouManager = LinearLayoutManager(this@VoteListActivity)
        recyclerViewVote.layoutManager = layouManager
        val mAdapter = VoteListAdapter(this@VoteListActivity)
        recyclerViewVote.adapter = mAdapter
        for(i in 1..5){
            voteList.add(VoteDTO("투표 제목$i", "2021-01-$i"))
        }
        mAdapter.list = voteList
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}