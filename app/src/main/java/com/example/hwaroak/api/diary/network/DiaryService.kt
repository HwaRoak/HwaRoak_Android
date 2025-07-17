package com.example.hwaroak.api.diary.network

import com.example.hwaroak.api.diary.model.DiaryRequest
import com.example.hwaroak.api.diary.model.DiaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


/**
 * network
 * 실제 사용되는 API 엔드포인트를 정의
 * API 타입(POST, GET)을 정의하고 suspend 메서드를 통해
 * Header랑 Body를 정의하여 송신 및 Response를 수신한다.
 * **/

interface DiaryService {


    @POST("api/v1/diary")
    suspend fun writeDiary(
        @Header("Authorization") token: String,
        @Body req: DiaryRequest
    ) : Response<DiaryResponse>
}