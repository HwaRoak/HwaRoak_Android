package com.example.hwaroak.api.mypage.repository

import android.util.Log
import com.example.hwaroak.api.mypage.model.AnalysisResponse
import com.example.hwaroak.api.mypage.model.EditProfileRequest
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import com.example.hwaroak.api.mypage.network.MemberService

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
}