package com.example.myapplication.dto.id

data class QuestionId (
        val question_id: Int
) {
    override fun toString(): String {
        return "QuestionId(question_id=$question_id)"
    }
}