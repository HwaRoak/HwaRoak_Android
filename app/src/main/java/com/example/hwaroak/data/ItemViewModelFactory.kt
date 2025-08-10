package com.example.hwaroak.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.api.home.repository.ItemRepository
import com.example.hwaroak.api.question.repository.QuestionRepository

class ItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemRepository, questionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}