package com.example.hwaroak.api.home.model

import com.google.gson.annotations.SerializedName

data class ItemDto(
    val itemId: Int,
    val name: String,
    val level: Int,
    val isSelected: Boolean
)

data class ApiResponse<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T?
)

//친구 아이템 리스트 조회
data class FriendItemListResponse(
    val items: List<Long>, //아이템 리스트
    val selectedItem: Long? //현재 선택된 아이템
)
