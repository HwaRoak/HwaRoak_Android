package com.example.hwaroak.api.diary.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.diary.model.DiaryEditRequest
import com.example.hwaroak.api.diary.model.DiaryEditResponse
import com.example.hwaroak.api.diary.model.DiaryWriteRequest
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import com.example.hwaroak.api.diary.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * access
 *
 * ViewModel은 UI 관련 데이터를 보관하는 아키텍처로 생명주기 변화를 견디는 역할을 수행한다.
 * fragment가 바뀌어도 데이터가 바뀌거나 초기화되지 않고 계속 유지한다.
 *
 * **/


class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    /**달력 작성 및 수정에 쓰는 변수**/
    //쓰기 모드인지 수정 모드인지 체크(1=쓰기/2=수정)
    private val _isWrite = MutableLiveData<Int>(1)
    val isWrite: LiveData<Int> = _isWrite
    //달력에서 오는 건지 결과에서 오는건지 (true = 달력/false = 쓰기)
    var isEditCalendar : Boolean = false
    //최신에 건든 diaryId
    var lastDiaryId : Int = 0
    /***************************/

    //일기 작성 결과 담기
    private val _writeResult = MutableLiveData<Result<DiaryWriteResponse>>()
    val writeResult: LiveData<Result<DiaryWriteResponse>> = _writeResult
    //일기 수정 결과 담기
    private val _editResult = MutableLiveData<Result<DiaryEditResponse>>()
    val editResult: LiveData<Result<DiaryEditResponse>> = _editResult


    //ViewModel에서 호출 -> repository에서 호출 -> network에서 호출
    //1. 일기 작성
    fun writeDiary(token: String, date: String, content: String, emotions: List<String>) {
        viewModelScope.launch {
            val res = repository.writeDiary(token, DiaryWriteRequest(date, content, emotions))
            _writeResult.postValue(res)
        }
    }


    //2. 일기 수정
    fun editDiary(token: String, id: Int, date: String, content: String, emotions: List<String>) {
        viewModelScope.launch {
            val res = repository.editDiary(token, id, DiaryEditRequest(date, content, emotions))
            _editResult.postValue(res)
        }
    }




    //isWrite 값 바꾸기
    fun setWriteMode() {
        _isWrite.value = 1
    }
    // 수정 모드로 전환
    fun setEditMode() {
        _isWrite.value = 2
    }
}