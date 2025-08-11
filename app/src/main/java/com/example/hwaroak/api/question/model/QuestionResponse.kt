package com.example.hwaroak.api.question.model

data class QuestionData(
    val content: String,
    val tag: String
)

data class QuestionResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: QuestionData?
)

data class ItemClickRequest(
    val tag: String
)

data class ItemClickResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: ItemClickData? // ItemClickData를 포함하도록 수정
)

data class ItemClickData(val content: String, val tag: String)