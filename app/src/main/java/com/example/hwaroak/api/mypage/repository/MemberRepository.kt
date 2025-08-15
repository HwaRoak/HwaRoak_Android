package com.example.hwaroak.api.mypage.repository

import android.content.Context
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
import com.example.hwaroak.api.mypage.network.MemberService
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException

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
        fileName: String,
        uri: Uri,
        context: android.content.Context
    ): Result<ConfirmUploadResponse> {
        return try {
            // 1) Presigned URL
            val req = PresignedUrlRequest(contentType, fileName)
            val presignedResp = service.getPresignedUrl("Bearer $token", req)
            val presignedBody = presignedResp.body()
            if (!presignedResp.isSuccessful || presignedBody == null) {
                return Result.failure(Exception("Presigned URL 발급 실패: ${presignedBody?.message ?: presignedResp.message()}"))
            }
            val data = presignedBody.data

            // 서버가 요구하는 Content-Type 헤더 값
            val headerContentType = data.requiredHeaders["Content-Type"] ?: contentType

            // 2) S3 PUT (본문 = Uri 스트리밍)
            val body = uriRequestBody(context, uri, headerContentType)
            val uploadResp = service.uploadImage(
                data.uploadUrl,
                headerContentType,
                body
            )
            if (!uploadResp.isSuccessful) {
                val err = uploadResp.errorBody()?.string()
                return Result.failure(Exception("PUT 실패: ${err ?: uploadResp.message()}"))
                Log.d("PUT", "PUT 실패: ${err ?: uploadResp.message()}")
            }

            // 3) 업로드 확정
            val confirmReq = ConfirmUploadRequest(data.objectKey)
            val confirmResp = service.confirmUpload("Bearer $token", confirmReq)
            val confirmBody = confirmResp.body()
            if (!confirmResp.isSuccessful || confirmBody == null) {
                return Result.failure(Exception("업로드 확정 실패: ${confirmBody?.message ?: confirmResp.message()}"))
            }

            Result.success(confirmBody.data) // ConfirmUploadResponse 반환
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun uriRequestBody(
        context: Context,
        uri: Uri,
        mime: String
    ): RequestBody = object : RequestBody() {
        override fun contentType(): MediaType? = mime.toMediaTypeOrNull()

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Cannot open InputStream for $uri")

            input.use { ins ->
                // okio.source() 안 씀. 표준 스트림으로 안전하게 복사
                sink.outputStream().use { outs ->
                    ins.copyTo(outs)
                }
            }
        }
    }
    suspend fun deleteImage(token: String): Result<Unit> {
        return try {
            val response = service.deleteImage("Bearer $token")
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val message = response.errorBody()?.string() ?: "에러 발생"
                Result.failure(Exception("삭제 실패: $message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}