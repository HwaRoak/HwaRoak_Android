package com.example.hwaroak.api.question.network

import com.example.hwaroak.api.question.model.QuestionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface QuestionService {
    @GET("api/v1/question")
    suspend fun getQuestion(@Header("Authorization") token: String)
            : Response<QuestionResponse>
}