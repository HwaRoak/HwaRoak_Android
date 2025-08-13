package com.example.hwaroak.api.question.repository

import com.example.hwaroak.api.question.model.QuestionData
import com.example.hwaroak.api.question.network.QuestionService

class QuestionRepository(private val service: QuestionService) {
    suspend fun fetch(accessToken: String): QuestionData? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        //로그 (삭제)
        val resp = service.getQuestion(token)
        android.util.Log.d("QuestionRepository",
            "http=${resp.code()} body=${resp.body()} tag=${resp.body()?.data?.tag}")

        return resp.body()?.data
        //
        return service.getQuestion(token).body()?.data
    }
}