package com.example.myapplication.dto.vote

data class EndVoteDTO(
        val question_id: Int,
        val done: Boolean
) {
    override fun toString(): String {
        return "EndVoteDTO(question_id=$question_id, done=$done)"
    }
}