package com.example.hwaroak.api.question.repository

import com.example.hwaroak.api.question.model.ItemClickData
import com.example.hwaroak.api.question.model.ItemClickRequest
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

    suspend fun itemClick(accessToken: String): ItemClickData? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        // fetch 함수를 호출하여 현재 아이템의 데이터를 가져옵니다.
        val itemClickData = fetch(accessToken) ?: return null

        // postItemClick을 호출하고 결과를 저장합니다.
        val res = service.postItemClick(token, ItemClickRequest(tag = itemClickData.tag))

        // 로그로 POST 호출 여부 확인용 (삭제)
        android.util.Log.d("QuestionRepository", "POST /question/item-click -> http=${res.code()} body=${res.body()}")

        return res.body()?.data
    }
}