package com.example.hwaroak.api.mypage.model

import android.text.Editable

data class MemberResponseBody<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T?
)

data class MemberInfoResponse(
    val memberId: Int,
    val userId: String,
    val nickname: String,
    val profileImgUrl: String?,
    val introduction: Editable?
)