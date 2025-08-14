package com.example.hwaroak.api.mypage.network

import com.example.hwaroak.api.mypage.model.AnalysisResponse
import com.example.hwaroak.api.mypage.model.ApiResponse
import com.example.hwaroak.api.mypage.model.ConfirmUploadRequest
import com.example.hwaroak.api.mypage.model.ConfirmUploadResponse
import com.example.hwaroak.api.mypage.model.EditProfileRequest
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import com.example.hwaroak.api.mypage.model.PresignedUrlRequest
import com.example.hwaroak.api.mypage.model.PresignedUrlResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

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

    @GET("api/v1/members/emotions/{summaryMonth}")
    suspend fun getAnalysis(
        @Header("Authorization") token: String,
        @Path("summaryMonth") summaryMonth: String
    ): Response<ApiResponse<AnalysisResponse>>

    @POST("api/v1/members/profile-image/upload-url")
    suspend fun getPresignedUrl(
        @Header("Authorization") token: String,
        @Body req: PresignedUrlRequest
    ): Response<ApiResponse<PresignedUrlResponse>>

    @PUT
    suspend fun uploadImage(
        @Url uploadUrl: String,
        @Header("Content-Type") type: String
    ): Response<Unit>

    @POST("api/v1/members/profile-image/confirm")
    suspend fun confirmUpload(
        @Header("Authorization") token: String,
        @Body req: ConfirmUploadRequest
    ): Response<ApiResponse<ConfirmUploadResponse>>

    @DELETE("api/v1/members/profile-image")
    suspend fun deleteImage(
        @Header("Authorization") token: String
    )

}