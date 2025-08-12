package com.example.hwaroak.api.friend.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hwaroak.api.friend.model.FriendResponse

//
class FriendNavViewModel : ViewModel() {
    private val _openFriendEvent = MutableLiveData<String?>()
    val openFriendEvent: LiveData<String?> = _openFriendEvent

    private val _friendListResult = MutableLiveData<Result<List<FriendResponse>>>()
    val friendListResult: LiveData<Result<List<FriendResponse>>> = _friendListResult //친구 목록 조회

    fun openFriend(friendId: String) { _openFriendEvent.value = friendId }



    fun clearOpenFriend() { _openFriendEvent.value = null }
}