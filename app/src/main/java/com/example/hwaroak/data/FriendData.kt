package com.example.hwaroak.ui.friend

data class FriendData(
    val name: String,
    val status: String,
    var isDeletable: Boolean = false  // 삭제 여부
)
