package com.example.hwaroak.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hwaroak.R
import com.example.hwaroak.data.LockerItem

class ItemViewModel : ViewModel() {
    private val _homeItemList = MutableLiveData<List<LockerItem>>()
    val homeItemList: LiveData<List<LockerItem>> = _homeItemList

    init {
        val initalItem = LockerItem(
            id = 0,
            name = "자물쇠",
            imageRes = R.drawable.img_item_lock
        )
        _homeItemList.value = listOf(initalItem)
    }

    fun setHomeItem(item: LockerItem) {
        _homeItemList.value = listOf(item)
    }
}