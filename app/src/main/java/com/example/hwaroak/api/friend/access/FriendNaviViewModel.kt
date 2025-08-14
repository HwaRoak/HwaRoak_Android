package com.example.hwaroak.api.friend.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hwaroak.api.friend.model.FriendResponse

//
class FriendNavViewModel : ViewModel() {
    private val _openFriendEvent = MutableLiveData<String?>()
    val openFriendEvent: LiveData<String?> = _openFriendEvent

    fun openFriend(friendId: String) { _openFriendEvent.value = friendId }



    fun clearOpenFriend() { _openFriendEvent.value = null }
}