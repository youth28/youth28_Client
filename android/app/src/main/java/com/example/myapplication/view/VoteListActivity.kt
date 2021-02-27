package com.example.myapplication.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.RoomData
import com.example.myapplication.adapter.VoteListAdapter
import com.example.myapplication.databinding.ActivityVoteListBinding
import com.example.myapplication.dialog.*
import com.example.myapplication.dialog.vote.PostVoteContentDialog
import com.example.myapplication.dto.vote.ItemVoteDTO
import com.example.myapplication.viewmodel.VoteListViewModel
import kotlinx.android.synthetic.main.activity_vote_list.*

class VoteListActivity : AppCompatActivity() {

    val TAG = "VoteListActivity"

    private lateinit var binding: ActivityVoteListBinding
    private lateinit var viewmodel: VoteListViewModel

    private val voteList = MutableLiveData<ArrayList<ItemVoteDTO>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("roomId")) {
            RoomData.roomId = intent.getIntExtra("roomId", 0)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_list)
        viewmodel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(VoteListViewModel::class.java)
        binding.lifecycleOwner=this
        binding.viewmodel = viewmodel
        binding.executePendingBindings()

        viewmodel.voteList.observe(this, { livedata ->
            voteList.value = livedata
            val layouManager = LinearLayoutManager(this@VoteListActivity)
            recyclerViewVote.layoutManager = layouManager
            val mAdapter = VoteListAdapter(this@VoteListActivity)
            recyclerViewVote.adapter = mAdapter
            mAdapter.list = voteList.value!!
        })

        with(viewmodel) {
            onCreateVoteEvent.observe(this@VoteListActivity, {
                val dialog = VoteMenuDialog()

                dialog.listener = { mode ->
                    if (mode == 1) {
//                        val dialog = DoVoteDialog()
//                        dialog.show(supportFragmentManager, "dialog")
                        showToast("현재 개발중 입니다.")
                    } else {
                        val dialog = PostVoteContentDialog()

                        dialog.listener = {title ->
                            showToast("[${title}]투표를 정상적으로 등록했습니다.")
                            onReadList()
                        }

                        dialog.show(supportFragmentManager, "dialog")
                    }
                }

                dialog.show(supportFragmentManager, "dialog")
            })
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}