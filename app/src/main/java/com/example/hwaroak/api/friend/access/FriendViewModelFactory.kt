package com.example.hwaroak.api.friend.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.friend.repository.FriendRepository

class FriendViewModelFactory(private val repository: FriendRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
