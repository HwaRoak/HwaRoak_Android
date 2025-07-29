package com.example.hwaroak.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.home.repository.ItemRepository

class ItemViewModelFactory(private val itemRepository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}