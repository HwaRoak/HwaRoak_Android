package com.example.hwaroak.api.friend.access

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.friend.model.FriendRequestResponse
import com.example.hwaroak.api.friend.model.FriendResponse
import com.example.hwaroak.api.friend.model.ReceivedFriendData
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.ui.friend.FriendData
import kotlinx.coroutines.launch

class FriendViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _friendListResult = MutableLiveData<Result<List<FriendResponse>>>()
    val friendListResult: LiveData<Result<List<FriendResponse>>> = _friendListResult //친구 목록 조회

    private val _searchedFriend = MutableLiveData<FriendData?>()
    val searchedFriend: LiveData<FriendData?> = _searchedFriend //친구 검색

    private val _friendRequestResult = MutableLiveData<Result<Unit>>()
    val friendRequestResult: LiveData<Result<Unit>> get() = _friendRequestResult //친구 요청 보내기

    private val _receivedRequests = MutableLiveData<List<ReceivedFriendData>>()
    val receivedRequests: LiveData<List<ReceivedFriendData>> get() = _receivedRequests //친구 요청 목록

    private val _acceptResult = MutableLiveData<Result<String>>()
    val acceptResult = MutableLiveData<Result<String>>()  //친구 요청 수락

    private val _rejectResult = MutableLiveData<Result<String>>()
    val rejectResult = MutableLiveData<Result<String>>() //친구 요청 거절

    private val _deleteResult = MutableLiveData<Result<String>>()
    val deleteResult: LiveData<Result<String>> get() = _deleteResult //친구 삭제(친구 목록에서)

    //친구 목록 조회
    fun fetchFriendList(token: String) {
       // Log.d("FriendViewModel", "fetchFriendList 실행됨")

        viewModelScope.launch {
            //Log.d("FriendViewModel", "Coroutine 시작됨")
            val result = repository.getFriendList(token)
            _friendListResult.postValue(result)
        }
    }

    //친구 검색
    fun searchFriend(token: String, userId: String) {
        viewModelScope.launch {
            val result = repository.searchFriend(token, userId)
            result.onSuccess { friend ->
                _searchedFriend.postValue(friend)
            }.onFailure {
                Log.e("FriendViewModel", "친구 요청 실패: ${it.message}", it)
                _searchedFriend.postValue(null)  // 타입 명시 필요 시: null as FriendData?
            }
        }
    }

    //친구 요청 보내기
    fun sendFriendRequest(token: String, receiverId: String) {
        viewModelScope.launch {
            val result = repository.sendFriendRequest(token, receiverId)
            _friendRequestResult.postValue(result)
        }
    }

    fun fetchReceivedFriendRequests(token: String) {
        viewModelScope.launch {
            try {
                val result = repository.getReceivedFriendRequests(token)
                result.onSuccess { list ->
                    if (list != null) {
                        _receivedRequests.postValue(list)
                    } else {
                        Log.w("FriendViewModel", "받은 친구 요청 결과가 null입니다")
                        _receivedRequests.postValue(emptyList())  // 또는 생략 가능
                    }
                }.onFailure {
                    Log.e("FriendViewModel", "받은 친구 요청 실패", it)
                }
            } catch (e: Exception) {
                Log.e("FriendViewModel", "예외 발생!", e)
            }
        }
    }

    //친구 요청 수락
    fun acceptFriend(token: String, friendId: String) {
        viewModelScope.launch {
            val result = repository.acceptFriendRequest(token, friendId)
            acceptResult.value = result.map { friendId }  // userId 그대로 전달
        }
    }


    //친구 요청 거절
    fun rejectFriend(token: String, friendId: String) {
        viewModelScope.launch {
            val result = repository.rejectFriendRequest(token, friendId)
            rejectResult.value = result.map { friendId }  // userId 그대로 전달
        }
    }

    //친구 삭제(목록에서)
    fun deleteFriend(token: String, friendId: String) {
        viewModelScope.launch {
            _deleteResult.value = repository.deleteFriend(token, friendId).map { friendId }
        }
    }

}
