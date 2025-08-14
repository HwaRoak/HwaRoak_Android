package com.example.hwaroak.api.mypage.repository

import android.net.Uri
import android.util.Log
import com.example.hwaroak.api.mypage.model.AnalysisResponse
import com.example.hwaroak.api.mypage.model.ConfirmUploadRequest
import com.example.hwaroak.api.mypage.model.ConfirmUploadResponse
import com.example.hwaroak.api.mypage.model.EditProfileRequest
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import com.example.hwaroak.api.mypage.model.PresignedUrlRequest
import com.example.hwaroak.api.mypage.model.PresignedUrlResponse
import com.example.hwaroak.api.mypage.network.MemberService
import com.google.api.Context
import okhttp3.RequestBody

class MemberRepository(private val service: MemberService) {

    suspend fun getMemberInfo(token: String): Result<MemberInfoResponse> {
        return try {
            val response = service.getMemberInfo("Bearer $token")
            val body = response.body()
            if (response.isSuccessful && body != null && body.status == "OK") {
                Log.d("MemberRepository", "회원 정보 조회 성공: ${body.data}")
                Result.success(body.data)
            } else {
                Log.e("MemberRepository", "회원 정보 조회 실패: status=${body?.status}, message=${body?.message}")
                Result.failure(Exception("회원 정보 조회 실패: ${body?.message ?: "알 수 없는 오류"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editProfile(
        token: String,
        nickname: String,
        introduction: String
    ): Result<EditProfileResponse> {
        return try {
            val req = EditProfileRequest(nickname, introduction)
            val response = service.editProfile("Bearer $token", req)
            val body = response.body()

            if (response.isSuccessful && body != null && body.status == "OK") {
                Log.d("MemberRepository", "프로필 수정 성공: ${body.data}")
                Result.success(body.data)
            } else {
                Log.e("MemberRepository", "프로필 수정 실패: status=${body?.status}, message=${body?.message}")
                Result.failure(Exception("프로필 수정 실패: ${body?.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMypageInfo(token: String): Result<MypageInfoResponse> {
        return try {
            val response = service.getMypageInfo("Bearer $token")
            val body = response.body()

            if (response.isSuccessful && body != null && body.status == "OK") {
                Log.d("MemberRepository", "마이페이지 정보 조회 성공: ${body.data}")
                Result.success(body.data)
            } else {
                Log.e("MemberRepository", "마이페이지 정보 조회 실패: status=${body?.status}, message=${body?.message}")
                Result.failure(Exception("마이페이지 정보 조회 실패: ${body?.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnalysisInfo(token: String, summaryMonth: String): Result<AnalysisResponse> {
        return try {
            val response = service.getAnalysis("Bearer $token", summaryMonth)
            val body = response.body()

            if (response.isSuccessful && body != null && body.status == "OK") {
                Log.d("MemberRepository", "감정분석 정보 조회 성공: ${body.data}")
                Result.success(body.data)
            } else {
                Log.e("MemberRepository", "감정분석 정보 조회 실패: status=${body?.status}, message=${body?.message}")
                Result.failure(Exception("감정분석 정보 조회 실패: ${body?.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(
        token: String,
        contentType: String,
        fileName: String
    ): Result<ConfirmUploadResponse> {
        return try {
            // 1) Presigned URL 발급
            val req = PresignedUrlRequest(contentType, fileName)
            val presignedResp = service.getPresignedUrl(token, req)
            val presignedBody = presignedResp.body()
            if (!presignedResp.isSuccessful || presignedBody == null) {
                return Result.failure(Exception("Presigned URL 발급 실패: ${presignedBody?.message}"))
            }
            val data = presignedBody.data  // PresignedUrlResponse

            // 2) S3 업로드 (PUT)
            val uploadResp = service.uploadImage(
                data.uploadUrl,
                data.requiredHeaders.Content_Type ?: contentType
            )
            if (!uploadResp.isSuccessful) {
                return Result.failure(Exception("PUT 실패: ${uploadResp.errorBody()?.string()}"))
            }

            // 3) 업로드 확정(확정 응답 전체를 반환)
            val confirmReq = ConfirmUploadRequest(data.objectKey)
            val confirmResp = service.confirmUpload("Bearer $token", confirmReq)
            val confirmBody = confirmResp.body()
            if (!confirmResp.isSuccessful || confirmBody == null) {
                return Result.failure(Exception("업로드 확정 실패: ${confirmBody?.message}"))
            }

            Result.success(confirmBody.data) // ← ConfirmUploadResponse
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}