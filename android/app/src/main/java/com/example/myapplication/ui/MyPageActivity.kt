package com.example.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.dialog.VoteDialog
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.dialog_vote.*

class MyPageActivity : AppCompatActivity() {

    val TAG = "MyPageActivity"

    val tagList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        // recyclerView setting
        val layouManager = LinearLayoutManager(this@MyPageActivity)
        layouManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewTag.layoutManager = layouManager
        recyclerViewTag.adapter = MyAdapter()

        createTAG()

        imgProfile.setOnClickListener {
            val dialog = VoteDialog()

            dialog.title = "오늘 뭐하지?"
            dialog.date = "2021-02-10"
            dialog.writer = "작성자: 누구누구"
            dialog.listener = {checkRB ->

            }
            dialog.show(supportFragmentManager, "dialog")
        }
    }

    fun createTAG() {
        for(i in 1..5){
            tagList.add("#태그$i")
        }
        recyclerViewTag.adapter?.notifyDataSetChanged()
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(this@MyPageActivity).inflate(R.layout.row_tag, parent, false))
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val tag = tagList[position]
            tag?.let {
                holder.tvTagName.text = tag
            }
        }

        override fun getItemCount(): Int {
            return tagList.size
        }

    }

    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTagName = itemView.findViewById<TextView>(R.id.tvTagName)
    }
}