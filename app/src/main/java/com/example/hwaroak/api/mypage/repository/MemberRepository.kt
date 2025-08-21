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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import okio.source
import java.io.IOException
import java.util.concurrent.TimeUnit
import com.example.hwaroak.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun stageProfileImage(
        token: String,
        contentType: String,
        fileName: String,
        uri: Uri,
        context: Context
    ): Result<String> { // returns objectKey
        return try {
            val req = PresignedUrlRequest(contentType, fileName)
            val presignedResp = service.getPresignedUrl("Bearer $token", req)
            val body = presignedResp.body() ?: return Result.failure(Exception("Presign 실패: ${presignedResp.message()}"))
            if (!presignedResp.isSuccessful) return Result.failure(Exception("Presign 실패: ${body.message}"))

            val data = body.data
            val headerContentType = data.requiredHeaders["Content-Type"] ?: contentType
            val rb = uriRequestBody(context, uri, headerContentType)

            // S3 PUT (IO에서 실행)
            val ok = withContext(Dispatchers.IO) {
                S3Uploader.putToS3(
                    uploadUrl = data.uploadUrl,
                    body = rb,
                    contentType = headerContentType,
                    extraHeaders = data.requiredHeaders
                )
            }
            require(ok) { "S3 업로드 실패" }

            // 서버 반영은 하지 않고 objectKey만 반환
            Result.success(data.objectKey)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun commitProfileImage(
        token: String,
        objectKey: String
    ): Result<ConfirmUploadResponse> {
        return try {
            val resp = service.confirmUpload("Bearer $token", ConfirmUploadRequest(objectKey))
            val body = resp.body() ?: return Result.failure(Exception("업로드 확정 실패: ${resp.message()}"))
            if (!resp.isSuccessful) return Result.failure(Exception("업로드 확정 실패: ${body.message}"))
            Result.success(body.data)
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

        // 2) 가능하면 Content-Length 제공 (chunked 회피)
        override fun contentLength(): Long {
            return try {
                context.contentResolver.openFileDescriptor(uri, "r")
                    ?.use { it.statSize }
                    ?.takeIf { it >= 0 } ?: -1L
            } catch (_: Exception) { -1L }
        }

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            context.contentResolver.openInputStream(uri)?.source().use { src ->
                requireNotNull(src) { "Cannot open InputStream for $uri" }
                sink.writeAll(src)
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

object S3Uploader {
    private val logger = HttpLoggingInterceptor { msg ->
        Log.d("S3HTTP", msg)
    }.apply {
        // 바디는 커다랠 수 있으니 HEADERS부터 시작, 필요시 BODY
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.HEADERS
        else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logger) // ✅ 디버그용
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(90, TimeUnit.SECONDS)
        // 인터셉터 없음(인증 추가 금지) 유지
        .build()

    fun putToS3(
        uploadUrl: String,
        body: RequestBody,
        contentType: String,
        extraHeaders: Map<String, String> = emptyMap()
    ): Boolean {
        val reqBuilder = Request.Builder()
            .url(uploadUrl)
            .put(body)

        if (extraHeaders.isNotEmpty()) {
            extraHeaders.forEach { (k, v) -> reqBuilder.header(k, v) }
        } else {
            reqBuilder.header("Content-Type", contentType)
        }

        Log.d("S3Uploader", "PUT 시작: url=$uploadUrl, headers=$extraHeaders") // ✅ 호출 전 로그

        okHttp.newCall(reqBuilder.build()).execute().use { res ->
            val ok = res.isSuccessful
            if (!ok) {
                val err = res.body?.string()
                Log.e("S3Uploader", "PUT 실패: code=${res.code}, msg=${res.message}, body=$err")
            } else {
                Log.d("S3Uploader", "PUT 성공: code=${res.code}")
            }
            return ok
        }
    }
}