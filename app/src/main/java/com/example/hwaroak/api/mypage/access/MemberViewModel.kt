package com.example.hwaroak.api.mypage.access

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.mypage.model.AnalysisResponse
import com.example.hwaroak.api.mypage.model.ConfirmUploadResponse
import com.example.hwaroak.api.mypage.model.EditProfileResponse
import com.example.hwaroak.api.mypage.model.MemberInfoResponse
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
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

//    private val _uploadResult = MutableLiveData<Result<ConfirmUploadResponse>>()
//    val uploadResult: LiveData<Result<ConfirmUploadResponse>> = _uploadResult

    private val _pendingObjectKey = MutableLiveData<String?>(null)
    val pendingObjectKey: LiveData<String?> = _pendingObjectKey

    private val _previewUri = MutableLiveData<Uri?>(null)
    val previewUri: LiveData<Uri?> = _previewUri

    private val _saveResult = MutableLiveData<Result<ConfirmUploadResponse>>()
    val saveResult: LiveData<Result<ConfirmUploadResponse>> = _saveResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _profileUpdated = MutableLiveData<Boolean>()
    val profileUpdated: LiveData<Boolean> = _profileUpdated

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
                setProfileUpdated()
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

    fun stageImage(token: String, uri: Uri, mime: String, fileName: String, ctx: Context) {
        viewModelScope.launch {
            // S3에 올려두고 objectKey만 보관
            val r = repository.stageProfileImage(token, mime, fileName, uri, ctx)
            r.onSuccess { key ->
                _pendingObjectKey.value = key
                _previewUri.value = uri // 로컬 프리뷰는 이걸로
            }.onFailure { e ->
                _pendingObjectKey.value = null
                _previewUri.value = null
                // 에러 처리(토스트 등) 호출부에서 observe
            }
        }
    }

    fun commitIfNeeded(token: String) {
        val key = _pendingObjectKey.value ?: run {
            _saveResult.value = Result.failure(IllegalStateException("보류 이미지 없음"))
            return
        }
        viewModelScope.launch {
            val r = repository.commitProfileImage(token, key)
            _saveResult.value = r
            if (r.isSuccess) {
                // 커밋 성공 후 보류 상태 해제
                _pendingObjectKey.value = null
                _previewUri.value = null
                setProfileUpdated()
            }
        }
    }

    fun discard(token: String) {
        // 선택: 보류 이미지 정리(백엔드가 지원하면 objectKey로 삭제 API 호출)
        _pendingObjectKey.value = null
        _previewUri.value = null
    }

    fun deleteProfileImage(token: String) {
        viewModelScope.launch {
            val result = repository.deleteImage(token)
            _deleteResult.postValue(result)
            if (result.isSuccess) {
                _pendingObjectKey.value = null
                _previewUri.value = null // 이미지 삭제 성공 시 임시 프리뷰 URI 제거
                setProfileUpdated()
            }
        }
    }

    // 프로필 수정/삭제 성공 시 호출
    fun setProfileUpdated() {
        _profileUpdated.value = true
    }

    // MypageFragment에서 업데이트 확인 후 상태 초기화
    fun resetProfileUpdated() {
        _profileUpdated.value = false
    }

    fun clearStagedImage() {
        _pendingObjectKey.value = null
        _previewUri.value = null
    }
}