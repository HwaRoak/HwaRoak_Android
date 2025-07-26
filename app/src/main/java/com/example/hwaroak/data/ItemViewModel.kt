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
            name = "default",
            imageRes = R.drawable.img_item_lock
        )
        _homeItemList.value = listOf(initalItem)
    }

    fun setHomeItem(item: LockerItem) {
        _homeItemList.value = listOf(item)
    }
    // 홈 보상 받기
    private val _rewardItemList = MutableLiveData<List<LockerItem>>()
    val rewardItemList: LiveData<List<LockerItem>> = _rewardItemList

    fun setRewardItems(items: List<LockerItem>) {
        _rewardItemList.value = items
    }
}

