package com.example.myapplication.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.dto.VoteDTO
import com.example.myapplication.R
import com.example.myapplication.ScheduleModel
import com.example.myapplication.adapter.VoteListAdapter
import com.example.myapplication.databinding.ActivityVoteListBinding
import com.example.myapplication.dialog.PostVoteContentDialog
import com.example.myapplication.dialog.PostVoteDialog
import com.example.myapplication.dialog.ScheduleDialog
import com.example.myapplication.viewmodel.VoteListViewModel
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.activity_vote_list.*

class VoteListActivity : AppCompatActivity() {

    val TAG = "VoteListActivity"

    private lateinit var binding: ActivityVoteListBinding
    private lateinit var viewmodel: VoteListViewModel

    val voteList = MutableLiveData<ArrayList<VoteDTO>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                val dialog = PostVoteContentDialog()

                dialog.listener = {date ->
                    Log.e("TAG", "등록했음: ${date}")
                }

                dialog.show(supportFragmentManager, "dialog")
            })
        }
    }

    fun showToast(str: String) {
        Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
    }
}