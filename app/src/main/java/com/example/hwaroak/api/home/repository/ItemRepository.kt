// ItemRepository.kt
package com.example.hwaroak.api.home.repository

import com.example.hwaroak.api.home.model.ApiResponse
import com.example.hwaroak.api.home.model.ItemDto
import com.example.hwaroak.api.home.network.ItemApiService // ItemApiService 임포트 확인
import com.example.hwaroak.api.question.network.QuestionService

class ItemRepository(private val service: ItemApiService) {

    // `token` 파라미터 추가
    suspend fun getItems(accessToken: String): List<ItemDto>? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getItemList(token) // token 전달
        return response.body()?.data
    }

    // `token` 파라미터 추가
    suspend fun getEquippedItem(accessToken: String): ItemDto? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getEquippedItem(token) // token 전달
        return response.body()?.data
    }

    // `token` 파라미터 추가 및 `service` 사용으로 통일
    suspend fun changeEquippedItem(accessToken: String, itemId: Int): ApiResponse<ItemDto>? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.changeEquippedItem(token, itemId) // token 전달
        return response.body()
    }
    // 아이템 획득
    suspend fun claimReward(accessToken: String): ItemDto? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.claimReward(token)
        return response.body()?.data
    }
}
