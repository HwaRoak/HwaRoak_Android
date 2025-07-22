package com.example.hwaroak.api.diary.access

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.api.diary.model.DiaryMonthResponse
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import com.example.hwaroak.api.diary.repository.DiaryRepository
import kotlinx.coroutines.launch

//달력 페이지에서 사용하는 viewModel
//DiaryViewModel과 동일한 repository를 사용하지만 사용처를 분리함
class CalendarViewModel(private val repository: DiaryRepository) : ViewModel()  {

    //월별 달력 내용
    private val _monthDiaryResult = MutableLiveData<Result<List<DiaryMonthResponse>>>()
    val monthDiaryResult: LiveData<Result<List<DiaryMonthResponse>>> = _monthDiaryResult



    //월별 일기 조회
    fun getMonthDiary(accessToken: String, year: Int, month: Int) {
        viewModelScope.launch {
            val res = repository.getMonthDiary(accessToken, year, month)
            _monthDiaryResult.postValue(res)
        }
    }

    //일기 삭제
    fun deleteDiary(accessToken: String, diaryID: Int) {
        viewModelScope.launch {
            repository.deleteDiary(accessToken, diaryID)

        }

    }

}