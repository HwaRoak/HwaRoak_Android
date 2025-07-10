package com.example.hwaroak.ui.friend

data class FriendData(
    val name: String,
    val status: String,
    val isAddButton: Boolean = false, // 버튼 여부
    var isDeletable: Boolean = false,  // 삭제 여부
    val isDeleteAllButton: Boolean = false //전체 삭제(추후 수정)
)
