package com.example.hwaroak.api.mypage.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.mypage.repository.MemberRepository

class MemberViewModelFactory(private val repository: MemberRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}