package com.example.myapplication.dto

data class ItemVoteDTO(
        val question_id: Int,
        val question_text: String,
        val time: String,
        val done: Int,
        val name: String
) {
    override fun toString(): String {
        return "ItemVoteDTO(question_id=$question_id, question_text='$question_text', time='$time', done=$done, name='$name')"
    }
}