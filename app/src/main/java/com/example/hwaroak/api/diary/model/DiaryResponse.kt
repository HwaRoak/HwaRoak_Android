package com.example.hwaroak.api.diary.model

/**
 * model
 * API 통수신을 위한 Request/Response 데이터 클래스를 정의하는 곳
 * HwaRoakClient에서 Data class <-> Json 변환 작업을 수행
 * **/

//1. 일기 작성 API 데이터 클래스
data class DiaryRequest(
    val recoredDate: String, //"2025-07-11"
    val emotion: String,    //"HAPPY"
    val nickname: String    //"오늘 일기 작성 내용"
)

data class DiaryResponse(
    val status: String,  //"OK" or "UNAUTHORIZED"
    val code: String,    //"SUCCESS" or "INVALID_TOKEN"
    val data: String?,   //성공 메시지
    val message: String? //실패 시 메시지
)
