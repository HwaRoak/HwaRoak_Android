package com.example.hwaroak.api.question.network

import com.example.hwaroak.api.question.model.ItemClickRequest
import com.example.hwaroak.api.question.model.ItemClickResponse
import com.example.hwaroak.api.question.model.QuestionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface QuestionService {
    @GET("api/v1/question")
    suspend fun getQuestion(@Header("Authorization") token: String, @Query("tag") tag: String? = null)
            : Response<QuestionResponse>

    @POST("api/v1/question/item-click")
    suspend fun postItemClick(
        @Header("Authorization") token: String,
        @Body body: ItemClickRequest
    ): Response<ItemClickResponse>
}