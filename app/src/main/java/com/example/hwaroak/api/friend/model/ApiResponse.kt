package com.example.hwaroak.api.friend.model

data class ApiResponse<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T
)