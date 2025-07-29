package com.example.hwaroak.api.friend.model

//친구 목록 조회 api 데이터클래스
data class FriendResponse(
    val userId: String,
    val nickname: String?,
    val introduction: String?
)

//친구 검색 api 데이터클래스
data class FriendSearchResponse(
    val userId: String,
    val nickname: String?,
    val introduction: String?
)

//친구 요청 보내기 api 데이터클래스
data class FriendRequestResponse(
    val friendRequestId: String,
    val receiverId: String,
    val status: String,
    val requestedAt: String
)

//친구 요청 목록 api 데이터클래스
data class ReceivedFriendResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<ReceivedFriendData>
)

//친구 삭제 api 데이터클래스
data class ReceivedFriendData(
    val userId: String,
    val nickname: String,
    val introduction: String?
)




