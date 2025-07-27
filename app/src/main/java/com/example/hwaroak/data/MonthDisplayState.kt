package com.example.hwaroak.data

data class MonthDisplayState(
    val previousMonthText: String,
    val currentMonthText: String,
    val nextMonthText: String,
    val isPreviousEnabled: Boolean, // '이전 달' 버튼 활성화 상태
    val isNextEnabled: Boolean      // '다음 달' 버튼 활성화 상태
)