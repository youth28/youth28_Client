package com.example.myapplication.dto

data class VoteDetailDTO(
        val question_text: String ,
        val content1: String,
        val content2: String,
        val content3: String,
        val content4: String,
        val content5: String,
        val time: String,
        val dead_line: String,
        val name: String
){
    override fun toString(): String {
        return "VoteDetailDTO(question_text='$question_text', content1='$content1', content2='$content2', content3='$content3', content4='$content4', content5='$content5', time='$time', dead_line='$dead_line', name='$name')"
    }
}