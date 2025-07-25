package com.example.hwaroak.api.diary.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
/**
 * model
 * API 통수신을 위한 Request/Response 데이터 클래스를 정의하는 곳
 * HwaRoakClient에서 Data class <-> Json 변환 작업을 수행
 * **/

//0. BODY
data class DiaryResponseBody<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T? = null
)

//1. 일기 조회 API 데이터 클래스(parameter만) - 일기 피드백 정보를 반환
//requestbody는 X
data class DiaryLookResponse(
    val id: Int,
    val recordDate: String,
    val emotionList: List<String>,
    val feedback: String,
)

//2. 일기 작성 API 데이터 클래스
data class DiaryWriteRequest(
    val recordDate: String, //"2025-07-11"
    val content: String,
    val emotionList: List<String>
)
data class DiaryWriteResponse(
    val id: Int,
    val recordDate: String,
    val emotionList: List<String>,
    val feedback: String,
    val reward: Int,
    val memberItemName: String
)

//3. 일기 상세 조회 API 데이터 클래스(parameter만) - 내 일기 내용을 반환
@Parcelize
data class DiaryDetailResponse(
    val id: Int,
    val recordDate: String,
    val emotionList: List<String>,
    val content: String
) : Parcelable

//4. 일기 삭제 API 데이터 클래스(없음)

//5. 일기 수정 API 데이터 클래스 (여기에 파라미터 붙이기)
data class DiaryEditRequest(
    val recordDate: String, //"2025-07-11"
    val content: String,
    val emotionList: List<String>
)
data class DiaryEditResponse(
    val id: Int,
    val recordDate: String,
    val emotionList: List<String>,
    val feedback: String,
    val reward: Int,
    val memberItemName: String
)


//6. 월별 일기 조회 API 데이터 클래스
data class DiaryMonthResponse(
    val id: Int,
    val recordDate: String,
    val emotionList: List<String>,
    val feedback: String,
)
