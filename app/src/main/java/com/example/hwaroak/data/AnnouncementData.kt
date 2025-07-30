package com.example.hwaroak.data

data class AnnouncementData(
    val id: Int,
    val title: String,
    val detail: String,
    val createdAt: String,
    var isExpanded: Boolean = false
)
