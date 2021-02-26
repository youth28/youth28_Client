package com.example.myapplication.dto.vote

data class VoteMakeDTO(
        val question_text: String ,
        val content1: String,
        val content2: String,
        val content3: String,
        val content4: String,
        val content5: String,
        val time: String,
        val dead_line: String,
        val done: Boolean,
        val room_id: Int,
        val user_id: Int
) {
    override fun toString(): String {
        return "VoteMakeDTO(question_text='$question_text', content1='$content1', content2='$content2', content3='$content3', content4='$content4', content5='$content5', time='$time', dead_line='$dead_line', done=$done, roomId=$room_id, userId=$user_id)"
    }
}
