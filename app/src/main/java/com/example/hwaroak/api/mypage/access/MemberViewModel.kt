package com.example.hwaroak.api.mypage.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.repository.MemberRepository
import kotlinx.coroutines.launch

class MemberViewModel(private val repository: MemberRepository): ViewModel() {

    // 회원 정보 조회 관련
    private val _memberInfoResult = MutableLiveData<Result<MemberInfoResponse>>()
    val memberInfoResult: LiveData<Result<MemberInfoResponse>> = _memberInfoResult

    // 회원 정보 수정 관련
    private val _editProfileResult = MutableLiveData<Result<EditProfileResponse>>()
    val editProfileResult: LiveData<Result<EditProfileResponse>> = _editProfileResult

    fun getMemberInfo(token: String) {
        viewModelScope.launch {
            val result = repository.getMemberInfo(token)
            _memberInfoResult.postValue(result)

        }
    }

    fun editProfile(token: String, nickname: String, profileImgUrl: String, introduction: String) {
        viewModelScope.launch {
            val result = repository.editProfile(token, nickname, profileImgUrl, introduction)
            _editProfileResult.postValue(result)

            if (result.isSuccess) {
                // 프로필 수정 후 최신 정보 다시 불러오기
                getMemberInfo(token)
            }
        }
    }

}