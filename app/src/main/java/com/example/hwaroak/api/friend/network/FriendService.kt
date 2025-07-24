package com.example.hwaroak.api.friend.network

import com.example.hwaroak.api.diary.model.DiaryRequest
import com.example.hwaroak.api.diary.model.DiaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
interface DiaryService {


    @GET("/api/vi/riends")
    suspend fun friendList(
        @Header("Authorization") token: String,
        @Body req: DiaryRequest
    ) : Response<DiaryResponse>
}