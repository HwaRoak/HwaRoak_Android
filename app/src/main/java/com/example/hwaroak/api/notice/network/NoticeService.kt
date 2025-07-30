package com.example.hwaroak.api.notice.network

import com.example.hwaroak.api.notice.model.AlarmListResponse
import com.example.hwaroak.api.notice.model.NoticeDetailResponse
import com.example.hwaroak.api.notice.model.NoticeListResponse
import com.example.hwaroak.api.notice.model.NoticeRegisterRequest
import com.example.hwaroak.api.notice.model.NoticeResponseBody
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NoticeService {

    //1. 공지 목록 조회 (이 경우 alarmType = Notification만 조회)
    @GET("api/v1/alarms/notices")
    suspend fun getNoticeList(
        @Header("Authorization") token: String
    ) : Response<NoticeResponseBody<List<NoticeListResponse>>>

    //2. 공지 등록
    @POST("api/v1/alarms/notices")
    suspend fun registerNotice(
        @Header("Authorization") token: String,
        @Body req: NoticeRegisterRequest
    ) : Response<NoticeResponseBody<Unit?>>

    //3. 알림 읽음 처리
    @PATCH("api/v1/alarms/{id}/read")
    suspend fun readNotice(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ) : Response<NoticeResponseBody<Unit?>>

    //4. 알림함 전체 조회 (이 경우 전체 다 조회 -> 알람에서 Notification이 공지)
    @GET("api/v1/alarms")
    suspend fun getAlarmList(
        @Header("Authorization") token: String
    ) : Response<NoticeResponseBody<List<AlarmListResponse>>>

    //5. 공지 상세 조회(알람 중 Notification인 거 터치 시 상세 조회)
    @GET("api/v1/alarms/{id}")
    suspend fun getDetailNotice(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ) : Response<NoticeResponseBody<NoticeDetailResponse>>
}