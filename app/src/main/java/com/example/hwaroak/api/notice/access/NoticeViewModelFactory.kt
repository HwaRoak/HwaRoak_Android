package com.example.hwaroak.api.notice.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.diary.access.DiaryViewModel
import com.example.hwaroak.api.notice.repository.NoticeRepository

class NoticeViewModelFactory(private val repository: NoticeRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(NoticeViewModel::class.java)) {

            // 의존성을 주입해 DiaryViewModel 인스턴스를 직접 생성해서 반환
            @Suppress("UNCHECKED_CAST")
            return NoticeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    }

}