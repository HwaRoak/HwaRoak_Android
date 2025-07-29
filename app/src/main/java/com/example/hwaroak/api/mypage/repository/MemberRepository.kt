package com.example.hwaroak.api.mypage.repository

import android.util.Log
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.network.MemberService

class MemberRepository(private val service: MemberService) {

    suspend fun getMemberInfo(token: String): Result<MemberInfoResponse> {
        return try {
            val response = service.getMemberInfo("Bearer $token")
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Log.d("Repository", "회원 정보 로딩 성공: ${body.data}")
                Result.success(body.data)
            } else {
                Log.e("Repository", "응답 실패: status=${body?.status}, message=${body?.message}")
                Result.failure(Exception("서버 응답 실패: ${body?.message ?: "알 수 없는 오류"}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}