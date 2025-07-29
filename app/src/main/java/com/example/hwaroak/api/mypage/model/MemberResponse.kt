package com.example.hwaroak.api.mypage.model

data class MemberInfoResponse(
    val memberId: Int,
    val userId: String,
    val nickname: String,
    val profileImgUrl: String?,
    val introduction: String?
)