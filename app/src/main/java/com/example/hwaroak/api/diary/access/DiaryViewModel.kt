package com.example.hwaroak.api.diary.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.diary.model.DiaryWriteRequest
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import com.example.hwaroak.api.diary.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
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

    //일기 작성 결과 담기
    private val _writeResult = MutableStateFlow<Result<DiaryWriteResponse>?>(null)
    val writeResult: StateFlow<Result<DiaryWriteResponse>?> = _writeResult

    //ViewModel에서 호출 -> repository에서 호출 -> network에서 호출
    fun writeDiary(token: String, req: DiaryWriteRequest) {
        viewModelScope.launch {
            _writeResult.value = repository.writeDiary(token, req)
        }
    }

}