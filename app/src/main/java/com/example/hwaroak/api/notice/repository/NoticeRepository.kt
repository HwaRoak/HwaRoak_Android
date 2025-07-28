package com.example.hwaroak.api.notice.repository

import com.example.hwaroak.api.diary.network.DiaryService
import com.example.hwaroak.api.notice.model.NoticeListResponse
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
}