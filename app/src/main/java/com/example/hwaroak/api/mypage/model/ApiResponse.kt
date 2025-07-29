package com.example.hwaroak.api.mypage.model

data class ApiResponse<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T
)
