package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RowVoteBinding
import com.example.myapplication.dialog.vote.DoContentVoteDialog
import com.example.myapplication.dialog.vote.NowVoteContentDialog
import com.example.myapplication.dto.vote.ItemVoteDTO

class VoteListAdapter (val context: Context): RecyclerView.Adapter<VoteListAdapter.Holder>() {
    var list = listOf<ItemVoteDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowVoteBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])

        holder.itemView.setOnClickListener {
            Log.e("click", list[position].done.toString())
            if (list[position].done == 0) {
                val dialog = DoContentVoteDialog()
                val manager = (context as AppCompatActivity).supportFragmentManager
                dialog.listener = { selectContent ->
                    showToast("[${selectContent}]에 투표하였습니다.")

                    val dialog = NowVoteContentDialog()
                    dialog.show(manager, "dialog")
                }
                dialog.questionId = list[position].question_id
                dialog.listener2 = {
                    showToast("[${it}]이(가) 마감되었습니다.")
                }
                dialog.show(manager, "dialog")
            } else {
                showToast("마감된 투표 입니다.")
                val manager = (context as AppCompatActivity).supportFragmentManager
                val dialog = NowVoteContentDialog()
                dialog.questionId = list[position].question_id
                dialog.show(manager, "dialog")
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(val binding: RowVoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(list: ItemVoteDTO) {
            binding.vote = list
        }
    }

    fun showToast(str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }
}