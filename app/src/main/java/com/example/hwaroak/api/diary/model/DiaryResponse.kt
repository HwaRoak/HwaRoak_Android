package com.example.hwaroak.api.diary.model

/**
 * model
 * API 통수신을 위한 Request/Response 데이터 클래스를 정의하는 곳
 * HwaRoakClient에서 Data class <-> Json 변환 작업을 수행
 * **/

//1. 일기 작성 API 데이터 클래스
data class DiaryWriteRequest(
    val recordDate: String, //"2025-07-11"
    val content: String,
    val emotion: String
)
data class DiaryWriteResponse(
    val id: Int,
    val feedback: String,
    val reward: Int,
    val item: String
)

//2. 일기 수정 API 데이터 클래스 (여기에 파라미터 붙이기)
data class DiaryEditRequest(
    val recordDate: String, //"2025-07-11"
    val content: String,
    val emotion: String
)
data class DiaryEditResponse(
    val id: Int,
    val feedback: String,
    val reward: Int,
    val item: String
)

//3. 일기 삭제 API 데이터 클래스


//4. 일기 조회 API 데이터 클래스(parameter만)
data class DiaryLookRequest(
    val date: String //"2025-07-11"
)
data class DiaryLookResponse(
    val id: Int,
    val feedback: String,
    val reward: Int,
    val item: String
)

//5. 월별 일기 조회 API 데이터 클래스
data class DiaryMonthRequest(
    val month: Int
)
data class DiaryMonthResponse(
    val id: Int,
    val feedback: String,
    val reward: Int,
    val item: String
)
