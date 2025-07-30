package com.example.hwaroak.api.notice.model

data class NoticeResponseBody<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T? = null
)

//1. 공지 목록 조회(request는 아무것도 X)
//alarmType = NOTIFICATION 인 공지를 최신순으로 조회합니다.
data class NoticeListResponse(
    val id: Int,
    val receiverId: Int,
    val title: String,
    val alarmType: String,
    val createdAt: String
)

//2. 공지 등록(response는 data 부분이 null)
data class NoticeRegisterRequest(
    val receiverId: Int,
    val senderId: Int,
    val title: String,
    val content: String,
    val message: String
)

//3. 알림 읽음 처리(등록된 공지의 id를 주면 읽음 처리)
//response의 경우 data 부분이 null

//4. 알림함 전체 조회
// 로그인한 사용자의 모든 알람을 최신순으로 조회합니다. (request는 아무것도 X)
data class AlarmListResponse(
    val id: Int,
    val title: String,
    val content: String,
    val alarmType: String,
    val createdAt: String
)

//5. 공지 상세 조회(공지 id를 파라미터로 주면 조회)
//alarmType = NOTIFICATION 인 공지의 상세 정보를 조회합니다.
//알람 리스트에서 touch 시 자세한 정보 보여주기
data class NoticeDetailResponse(
    val id: Int,
    val title: String,
    val content: String,
    val alarmType: String,
    val createdAt: String
)

//6. 알람 설정 조회


