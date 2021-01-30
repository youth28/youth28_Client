package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DTO.VoteDTO
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_vote_list.*
import kotlinx.android.synthetic.main.row_vote.*

class VoteListActivity : AppCompatActivity() {

    val TAG = "VoteListActivity"

    val voteList = mutableListOf<VoteDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_list)

        // recyclerView setting
        val layouManager = LinearLayoutManager(this@VoteListActivity)
        recyclerViewVote.layoutManager = layouManager
        recyclerViewVote.adapter = MyAdapter()

        createVote()
    }

    fun createVote() {
        for(i in 1..5){
            voteList.add(VoteDTO("투표 제목$i", "2021-01-$i"))
        }
        recyclerViewVote.adapter?.notifyDataSetChanged()
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@VoteListActivity).inflate(R.layout.row_vote, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val vote = voteList[position]
            vote?.let {
                holder.tvVoteTitle.text = vote.title
                holder.tvVoteDate.text = vote.date
            }
        }

        override fun getItemCount(): Int {
            return voteList.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvVoteTitle = itemView.findViewById<TextView>(R.id.tvVoteTitle)
        val tvVoteDate = itemView.findViewById<TextView>(R.id.tvVoteDate)
    }
}