package com.example.myapplication.dto.vote

data class VoteStatusDTO (
        val question_text: String ,
        val content1: String,
        val content2: String,
        val content3: String,
        val content4: String,
        val content5: String,
        val content1_cnt: Int,
        val content2_cnt: Int,
        val content3_cnt: Int,
        val content4_cnt: Int,
        val content5_cnt: Int,
        val time: String,
        val dead_line: String,
        val name: String
        ) {
    override fun toString(): String {
        return "VoteStatusDTO(question_text='$question_text', content1='$content1', content2='$content2', content3='$content3', content4='$content4', content5='$content5', content1_cnt=$content1_cnt, content2_cnt=$content2_cnt, content3_cnt=$content3_cnt, content4_cnt=$content4_cnt, content5_cnt=$content5_cnt, time='$time', dead_line='$dead_line', name='$name')"
    }
}