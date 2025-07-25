package com.example.hwaroak.api.diary.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.diary.repository.DiaryRepository

class CalendarViewModelFactory(private val repository: DiaryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {

            // 의존성을 주입해 DiaryViewModel 인스턴스를 직접 생성해서 반환
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}