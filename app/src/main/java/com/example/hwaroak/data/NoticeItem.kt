package com.example.hwaroak.data

data class NoticeItem(
    val id: Int,
    val title: String,
    val content: String,
    val alarmType: String,
    val isRead: Boolean,
    val createdAt: String,
    val userId: String
)
