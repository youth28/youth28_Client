package com.example.myapplication.dto

data class DoVoteDTO (
        val select_text: String,
        val user_id: Int,
        val question_id: Int
        ) {
    override fun toString(): String {
        return "DoVoteDTO(select_text='$select_text', user_id=$user_id, question_id=$question_id)"
    }
}