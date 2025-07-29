package com.example.hwaroak.api.mypage.network

import com.example.hwaroak.api.mypage.model.ApiResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface MemberService {

    @GET("api/v1/members")
    suspend fun getMemberInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<MemberInfoResponse>>
}