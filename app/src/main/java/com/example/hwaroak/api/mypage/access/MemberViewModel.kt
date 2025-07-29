package com.example.hwaroak.api.mypage.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.repository.MemberRepository
import kotlinx.coroutines.launch

class MemberViewModel(private val repository: MemberRepository): ViewModel() {

    // 회원 정보 담기
    private val _memberInfoResult = MutableLiveData<Result<MemberInfoResponse>>()
    val memberInfoResult: LiveData<Result<MemberInfoResponse>> = _memberInfoResult

//    // 프로필 업데이트 성공 여부 담기
//    private val _updateSuccess = MutableLiveData<Boolean>()
//    val updateSuccess: LiveData<Boolean> = _updateSuccess

    fun getMemberInfo(token: String) {
        viewModelScope.launch {
            val result = repository.getMemberInfo(token)
            _memberInfoResult.postValue(result)

        }
    }

}