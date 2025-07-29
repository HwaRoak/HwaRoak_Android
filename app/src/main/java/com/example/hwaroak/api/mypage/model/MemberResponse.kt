package com.example.hwaroak.api.mypage.model

// 1. 회원정보 조회
data class MemberInfoResponse(
    val memberId: Int,
    val userId: String,
    val nickname: String,
    val profileImgUrl: String?,
    val introduction: String?
)

// 2. 회원 정보 수정
data class EditProfileRequest(
    val nickname: String,
    val profileImageUrl: String,
    val introduction: String
)
data class EditProfileResponse(
    val memberId: Int?,          // null 가능성 있음
    val userId: String?,         // null 가능성 있음
    val nickname: String,        // 서버에서 항상 내려온다면 non-null
    val profileImgUrl: String?,  // null 또는 "" 가능
    val introduction: String     // 항상 있음
)