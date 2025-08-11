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
    val introduction: String
)
data class EditProfileResponse(
    val memberId: Int?,          // null 가능성 있음
    val userId: String?,         // null 가능성 있음
    val nickname: String,        // 서버에서 항상 내려온다면 non-null
    val profileImgUrl: String?,  // null 또는 "" 가능
    val introduction: String     // 항상 있음
)

// 3. 마이페이지 정보 조회
data class MypageInfoResponse(
    val nickname: String,
    val profileImgUrl: String?,  // null 또는 "" 가능
    val emotionSummary: EmotionSummary?,
    val totalDiary: Int,
    val reward: Int,
    val nextItemName: String
)

data class EmotionSummary(
    val CALM: EmotionData,
    val HAPPY: EmotionData,
    val SAD: EmotionData,
    val ANGRY: EmotionData
)

data class EmotionData(
    val number: Int,
    val percent: Double
)

data class AnalysisResponse(
    val diaryCount: Int,
    val emotionSummary: EmotionSummary?,
    val message: String
)