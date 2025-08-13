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