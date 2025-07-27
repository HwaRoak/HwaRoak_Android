package com.example.hwaroak.api.home.network

import com.example.hwaroak.api.home.model.ApiResponse
import com.example.hwaroak.api.home.model.ItemDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ItemApiService {

    // 1. 보유 아이템 리스트 조회
    @GET("api/v1/members/items")
    suspend fun getItemList(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<ItemDto>>>

    // 2. 대표 아이템 조회
    @GET("api/v1/members/items/selected")
    suspend fun getEquippedItem(
        @Header("Authorization") token: String
    ): Response<ApiResponse<ItemDto>>

    // 3. 대표 아이템 변경
    @PATCH("api/v1/members/items/{itemId}/selected")
    suspend fun changeEquippedItem(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: Int
    ): Response<ApiResponse<ItemDto>>
}
