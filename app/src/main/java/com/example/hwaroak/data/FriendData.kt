package com.example.hwaroak.ui.friend

data class FriendData(
    val name: String, //이름
    val status: String = "", //자기소개
    val id: String = "", //아이디
    val isAddButton: Boolean = false, // 버튼 여부
    var isDeletable: Boolean = false,  // 삭제 여부
    var isRequested: Boolean = false, // 요청 보냈는지 여부
    var isFriend: Boolean = false //친구 여부
)
