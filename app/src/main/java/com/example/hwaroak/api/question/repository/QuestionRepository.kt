package com.example.hwaroak.api.question.repository

import com.example.hwaroak.api.question.model.QuestionData
import com.example.hwaroak.api.question.network.QuestionService

class QuestionRepository(private val service: QuestionService) {
    suspend fun fetch(accessToken: String): QuestionData? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        return service.getQuestion(token).body()?.data
    }
}