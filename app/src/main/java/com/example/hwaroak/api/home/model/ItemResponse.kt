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
