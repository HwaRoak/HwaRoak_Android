package com.example.hwaroak.api.notice.repository

import android.util.Log
import com.example.hwaroak.api.diary.network.DiaryService
import com.example.hwaroak.api.notice.model.AlarmListResponse
import com.example.hwaroak.api.notice.model.AlarmSettingRequest
import com.example.hwaroak.api.notice.model.AlarmSettingResponse
import com.example.hwaroak.api.notice.model.NoticeDetailResponse
import com.example.hwaroak.api.notice.model.NoticeListResponse
import com.example.hwaroak.api.notice.model.NoticeRegisterRequest
import com.example.hwaroak.api.notice.network.NoticeService

class NoticeRepository(private val service: NoticeService) {

    //1. 공지 목록 조회(alarmType = Notification인 것들만 최신순 리스트 get)
    suspend fun getNoticeList(
        accessToken: String
    ) : Result<List<NoticeListResponse>> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getNoticeList(token)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }

    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //2. 공지 등록
    suspend fun registerNotice(
        accessToken: String, req: NoticeRegisterRequest
    ) : Result<Unit?> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.registerNotice(token, req)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류
        Result.failure(e)
    }


    //3. 알림 읽음 처리(느슨하게 처리)
    suspend fun readNotice(
        accessToken: String, id: Int
    ) = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.readNotice(token, id)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //4. 알림함 전체 조회(로그인한 사용자의 모든 알람을 최신순으로 조회)
    suspend fun getAlarmList(
        accessToken: String
    ) : Result<List<AlarmListResponse>> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getAlarmList(token)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }

    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //5. 공지 상세 조회(공지 id를 파라미터로 주면 조회)
    suspend fun getDetailNotice(
    accessToken: String, noticeId: Int
    ) : Result<NoticeDetailResponse> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getDetailNotice(token, noticeId)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //6. 알람 세팅 불러오기
    suspend fun getAlarmSetting(accessToken: String) : Result<AlarmSettingResponse> =
        try{
            val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
            val response = service.getAlarmSetting(token)
            if(response.isSuccessful){
                val body = response.body()
                //body가 null일 때
                if(body == null){
                    Result.failure(RuntimeException("Response body is null"))
                }
                //data 값이 없을 때
                else if(body.data == null){
                    Result.failure(RuntimeException("Response OK but Data is null"))
                }
                else {
                    Result.success(body.data)
                }
            }
            else{
                val errMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
            }
    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //7. 알람 설정 변경
    suspend fun setAlarmSetting(accessToken: String, req: AlarmSettingRequest)
    : Result<Unit?> = try {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.setAlarmSetting(token, req)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Log.d("log_setting", "body is null")
                Result.failure(RuntimeException("Response body is null"))
            }
            else if(body.status != "OK"){
                Log.d("log_setting", "Status is not OK")
                Result.failure(RuntimeException("Not ok"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Log.d("log_setting", "요청 실패(isnotSuccess): $errMsg")
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류
        Log.d("log_setting", "try catch: $e")
        Result.failure(e)
    }

}