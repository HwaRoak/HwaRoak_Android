package com.example.hwaroak.data

import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.home.repository.ItemRepository // ItemRepository 임포트 추가
import com.example.hwaroak.api.home.model.ItemDto // ItemDto 임포트 추가
import kotlinx.coroutines.launch

// ItemRepository를 생성자로 주입받도록 변경합니다.
// 이 ViewModel을 사용하는 곳(예: HomeFragment, LockerFragment)에서 ItemRepository 인스턴스를 넘겨줘야 합니다.
class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    private val _homeItemList = MutableLiveData<List<LockerItem?>>()
    val homeItemList: LiveData<List<LockerItem?>> = _homeItemList

    init {
        viewModelScope.launch {
            // HwaRoakClient.currentAccessToken이 null이 아닐 때만 API 호출
            HwaRoakClient.currentAccessToken?.let { token ->
                val equippedItemDto = itemRepository.getEquippedItem(token) // 토큰 전달
                equippedItemDto?.let {
                    val initialItem = LockerItem(
                        id = it.itemId,
                        name = it.name,
                        imageRes = getImageResForName(it.name)
                    )
                    _homeItemList.value = listOf(initialItem)
                }
            } ?: run {
                // 토큰이 없는 경우 (예: 로그인 안 된 상태), 적절한 처리 (오류 메시지, 기본 아이템 등)
                // Log.e("ItemViewModel", "Access token is null, cannot fetch equipped item.")
                _homeItemList.value = emptyList() // 또는 기본 아이템 설정
            }
        }
    }

    // 홈 화면에 표시될 아이템을 변경할 때 호출되는 함수입니다.
    fun setHomeItem(item: LockerItem) {
        viewModelScope.launch {
            HwaRoakClient.currentAccessToken?.let { token ->
                val response = itemRepository.changeEquippedItem(token, item.id) // 토큰 전달
                if (response?.status == "OK") {
                    // 성공 처리 (예: _homeItemList 업데이트)
                    _homeItemList.value = listOf(item)
                } else {
                    // 실패 처리 (예: 오류 메시지 토스트)
                    Log.e("ItemViewModel", "Failed to change item: ${response?.message}")
                }
            } ?: run {
                Log.e("ItemViewModel", "Access token is null, cannot change home item.")
            }
        }
    }

    // 보상으로 받은 아이템 목록을 설정하는 부분 (이 부분은 API 연결과 직접적인 관련이 적고 기존과 동일)
    private val _rewardItemList = MutableLiveData<List<LockerItem?>>()
    val rewardItemList: LiveData<List<LockerItem?>> = _rewardItemList

    fun setRewardItems(items: List<LockerItem?>) {
        _rewardItemList.value = items
    }

    // 모든 보유 아이템을 불러오는 함수
    fun loadUserItems() {
        viewModelScope.launch {
            HwaRoakClient.currentAccessToken?.let { token ->
                val items = itemRepository.getItems(token) // 토큰 전달
                val lockerItems = items?.map {
                    LockerItem(it.itemId, it.name, getImageResForName(it.name))
                } ?: emptyList()
                _rewardItemList.value = lockerItems
            } ?: run {
                // Log.e("ItemViewModel", "Access token is null, cannot fetch reward items.")
                _rewardItemList.value = emptyList()
            }
        }
    }

    // 보상획득 로직
    fun claimReward(onResult: (LockerItem?) -> Unit) {
        viewModelScope.launch {
            val token = HwaRoakClient.currentAccessToken
            if (token != null) {
                val rewardedItemDto = itemRepository.claimReward(token)
                rewardedItemDto?.let {
                    val rewardedItem = LockerItem(
                        it.itemId,
                        it.name,
                        getImageResForName(it.name)
                    )
                    // 홈 아이템으로 설정
                    _homeItemList.value = listOf(rewardedItem)
                    // 보유 아이템 목록 갱신
                    loadUserItems()
                    onResult(rewardedItem)
                } ?: run {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
    }


    // ItemDto의 name(문자열)에 따라 안드로이드 drawable 리소스 ID(정수)를 반환하는 헬퍼 함수입니다.
    // 이 함수에 모든 아이템 이름과 해당 이미지 리소스 매핑을 추가해야 합니다.
    // ItemViewModel.kt
    private fun getImageResForName(itemName: String): Int {
        return when (itemName) {
            "자물쇠" -> R.drawable.img_item_lock // 이 부분은 유지
            "cheeze" -> R.drawable.img_item_cheeze // R.id 대신 R.drawable
            "chicken" -> R.drawable.img_item_chicken // R.id 대신 R.drawable
            "chopstick" -> R.drawable.img_item_chopstick // R.id 대신 R.drawable
            "coal" -> R.drawable.img_item_coal // R.id 대신 R.drawable
            "cup" -> R.drawable.img_item_cup // R.id 대신 R.drawable
            "egg" -> R.drawable.img_item_egg // R.id 대신 R.drawable
            "mashmellow" -> R.drawable.img_item_mashmellow // R.id 대신 R.drawable
            "meat" -> R.drawable.img_item_meat // R.id 대신 R.drawable
            "paper" -> R.drawable.img_item_paper // R.id 대신 R.drawable
            "potato" -> R.drawable.img_item_potato // R.id 대신 R.drawable
            "ruby" -> R.drawable.img_item_ruby // R.id 대신 R.drawable (만약 이 리소스가 있다면)
            "soup" -> R.drawable.img_item_soup // R.id 대신 R.drawable
            "tire" -> R.drawable.img_item_tire // R.id 대신 R.drawable
            "tissue" -> R.drawable.img_item_tissue // **여기! R.id 대신 R.drawable.img_item_tissue 로 변경**
            "trash" -> R.drawable.img_item_trash // R.id 대신 R.drawable
            else -> R.drawable.img_item_lock // 기본값은 자물쇠 이미지로 유지
        }
    }

// ... (나머지 코드)
}