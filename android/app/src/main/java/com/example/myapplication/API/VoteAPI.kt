package com.example.myapplication.api

import com.example.myapplication.dto.id.QuestionId
import com.example.myapplication.dto.id.RoomId
import com.example.myapplication.dto.vote.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface VoteAPI {
    @POST("question_make")
    fun question_make(
            @Body voteMakeDTO: VoteMakeDTO
    ): Call<ResponseBody>

    @POST("vote")
    fun vote(
            @Body doVoteDTO: DoVoteDTO
    ): Call<ResponseBody>

    @POST("look_vote")
    fun look_vote(
            @Body questionId: QuestionId
    ): Call<VoteDetailDTO>

    @POST("now_vote")
    fun now_vote(
            @Body questionId: QuestionId
    ): Call<VoteStatusDTO>

    @POST("end_vote")
    fun end_vote(
            @Body endVoteDTO: EndVoteDTO
    ): Call<ResponseBody>

    @POST("list_vote")
    fun list_vote(
            @Body roomId: RoomId
    ): Call<VoteListDTO>
}