package com.example.hwaroak.api.question.repository

import android.util.Log
import com.example.hwaroak.api.question.model.QuestionData
import com.example.hwaroak.api.question.network.QuestionService

class QuestionRepository(private val service: QuestionService) {

    // 기본 말풍선 로직
    suspend fun fetch(accessToken: String): QuestionData? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val resp = service.getQuestion(token)
        android.util.Log.d("QuestionRepository",
            "http=${resp.code()} body=${resp.body()} tag=${resp.body()?.data?.tag}")

        return resp.body()?.data
        //
        return service.getQuestion(token).body()?.data
    }

    // 아이템 클릭시 말풍선 로직
    suspend fun fetchItemClickQuestion(accessToken: String, itemTag: String): String? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"

        // ✅ 요청 로그: 어떤 아이템 태그로 API를 호출하는지 확인
        Log.d("QuestionRepository", "아이템 클릭 API 요청: tag=${itemTag}")


        val response = service.getQuestionForItemClick(token, itemTag)

        // ✅ 응답 로그: 서버의 응답 코드와 전체 응답 내용을 확인
        Log.d("QuestionRepository", "아이템 클릭 API 응답: code=${response.code()}, body=${response.body()}")


        return response.body()?.data?.content
    }
}