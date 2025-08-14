package com.example.hwaroak.api.mypage.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.mypage.model.AnalysisResponse
import com.example.hwaroak.api.mypage.model.ConfirmUploadResponse
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import com.example.hwaroak.api.mypage.model.PresignedUrlResponse
import com.example.hwaroak.api.mypage.repository.MemberRepository
import kotlinx.coroutines.launch

class MemberViewModel(private val repository: MemberRepository): ViewModel() {

    // 회원 정보 조회 관련
    private val _memberInfoResult = MutableLiveData<Result<MemberInfoResponse>>()
    val memberInfoResult: LiveData<Result<MemberInfoResponse>> = _memberInfoResult

    // 회원 정보 수정 관련
    private val _editProfileResult = MutableLiveData<Result<EditProfileResponse>>()
    val editProfileResult: LiveData<Result<EditProfileResponse>> = _editProfileResult

    private val _mypageInfoResult = MutableLiveData<Result<MypageInfoResponse>>()
    val mypageInfoResult: LiveData<Result<MypageInfoResponse>> = _mypageInfoResult

    private val _analysisResult = MutableLiveData<Result<AnalysisResponse>>()
    val analysisResult: LiveData<Result<AnalysisResponse>> = _analysisResult

    private val _uploadResult = MutableLiveData<Result<ConfirmUploadResponse>>()
    val uploadResult: LiveData<Result<ConfirmUploadResponse>> = _uploadResult
    fun getMemberInfo(token: String) {
        viewModelScope.launch {
            val result = repository.getMemberInfo(token)
            _memberInfoResult.postValue(result)

        }
    }

    fun editProfile(token: String, nickname: String, introduction: String) {
        viewModelScope.launch {
            val result = repository.editProfile(token, nickname, introduction)
            _editProfileResult.postValue(result)

            if (result.isSuccess) {
                // 프로필 수정 후 최신 정보 다시 불러오기
                getMemberInfo(token)
            }
        }
    }

    fun getMypageInfo(token: String) {
        viewModelScope.launch {
            val result = repository.getMypageInfo(token)
            _mypageInfoResult.postValue(result)
        }
    }

    fun getAnalysisInfo(token: String, summaryMonth: String) {
        viewModelScope.launch {
            val result = repository.getAnalysisInfo(token, summaryMonth)
            _analysisResult.postValue(result)
        }
    }

    fun uploadProfileImage(token: String, contentType: String, fileName: String) {
        viewModelScope.launch {
            val result = repository.uploadProfileImage(token, contentType, fileName)
            _uploadResult.postValue(result)
        }
    }
}