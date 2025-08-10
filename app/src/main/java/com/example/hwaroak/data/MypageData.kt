package com.example.hwaroak.data

data class MypageData(
    val emotionSummary: EmotionSummary?,
    val totalDiary: Int, // 기록한 일기 수
    val itemId: String, // 다음 아이템 id
    val reward: Int // 다음 아이템을 받기까지 남은 시간
)

data class MyinfoData(
    val name: String, // 이름
    val id: String = "", // 유저 아이디
    val introduction: String = "", // 자기소개
)
data class EmotionSummary(
    val CALM: EmotionData,
    val HAPPY: EmotionData,
    val SAD: EmotionData,
    val ANGRY: EmotionData
)

data class EmotionData(
    val percent: Double
)