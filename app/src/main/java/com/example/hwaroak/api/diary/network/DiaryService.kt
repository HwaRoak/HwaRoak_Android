package com.example.hwaroak.api.diary.network

import com.example.hwaroak.api.diary.model.DiaryDetailResponse
import com.example.hwaroak.api.diary.model.DiaryEditRequest
import com.example.hwaroak.api.diary.model.DiaryEditResponse
import com.example.hwaroak.api.diary.model.DiaryLookResponse
import com.example.hwaroak.api.diary.model.DiaryMonthResponse
import com.example.hwaroak.api.diary.model.DiaryResponseBody
import com.example.hwaroak.api.diary.model.DiaryWriteRequest
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * network
 * 실제 사용되는 API 엔드포인트를 정의
 * API 타입(POST, GET)을 정의하고 suspend 메서드를 통해
 * Header랑 Body를 정의하여 송신 및 Response를 수신한다.
 * **/

interface DiaryService {

    //1. 일기 조회 API
    @GET("api/v1/diary")
    suspend fun getDiary(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<DiaryResponseBody<DiaryLookResponse>>

    //2. 일기 작성 API
    @POST("api/v1/diary")
    suspend fun writeDiary(
        @Header("Authorization") token: String,
        @Body req: DiaryWriteRequest
    ) : Response<DiaryResponseBody<DiaryWriteResponse>>

    //3. 일기 상세 조회 API
    /**response 차후 정의**/
    @GET("api/v1/diary/{diaryId}")
    suspend fun getDetailDiary(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int,
    ) : Response<DiaryResponseBody<DiaryDetailResponse>>

    //4. 일기 삭제 API
    @DELETE("api/v1/diary/{diaryId}")
    suspend fun deleteDiary(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int
    ) : Response<Unit>

    //5. 일기 수정 API
    @PATCH("api/v1/diary/{diaryId}")
    suspend fun editDiary(
        @Header("Authorization") token: String,
        @Path("diaryId") diaryId: Int,
        @Body req: DiaryEditRequest
    ) : Response<DiaryResponseBody<DiaryEditResponse>>


    //6. 월별 일기 조회 API
    @GET("api/v1/diary/monthly")
    suspend fun getMonthDiary(
        @Header("Authorization") token: String,
        @Query("year")  year: Int,
        @Query("month") month: Int
    ) : Response<DiaryResponseBody<List<DiaryMonthResponse>>>
}