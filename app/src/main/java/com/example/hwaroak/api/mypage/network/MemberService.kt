package com.example.hwaroak.api.mypage.network

import com.example.hwaroak.api.mypage.model.ApiResponse
import com.example.hwaroak.api.mypage.model.EditProfileRequest
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface MemberService {

    @GET("api/v1/members")
    suspend fun getMemberInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<MemberInfoResponse>>

    @PATCH("api/v1/members")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Body req: EditProfileRequest
    ): Response<ApiResponse<EditProfileResponse>>

    @GET("api/v1/members/preview")
    suspend fun getMypageInfo(
        @Header("Authorization") token: String,
    ): Response<ApiResponse<MypageInfoResponse>>

}