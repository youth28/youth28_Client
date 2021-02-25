package com.example.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RowVoteBinding
import com.example.myapplication.dialog.DoVoteDialog
import com.example.myapplication.dto.ItemVoteDTO

class VoteListAdapter (val context: Context): RecyclerView.Adapter<VoteListAdapter.Holder>() {
    var list = listOf<ItemVoteDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = RowVoteBinding.inflate(LayoutInflater.from(context), parent, false)

        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(list[position])

        holder.itemView.setOnClickListener {
            Log.e("click", list[position].question_text)
            val dialog = DoVoteDialog()
            dialog.title = list[position].question_text
            dialog.date = list[position].time
            dialog.writer = list[position].name
            dialog.listener = { checkRB ->
                showToast("'${checkRB}'에 투표하였습니다.")
            }
            val manager = (context as AppCompatActivity).supportFragmentManager
            dialog.show(manager, "dialog")
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