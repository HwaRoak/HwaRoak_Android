// ItemRepository.kt
package com.example.hwaroak.api.home.repository

import com.example.hwaroak.api.home.model.ApiResponse
import com.example.hwaroak.api.home.model.ItemDto
import com.example.hwaroak.api.home.network.ItemApiService // ItemApiService 임포트 확인

class ItemRepository(private val service: ItemApiService) {

    // `token` 파라미터 추가
    suspend fun getItems(accessToken: String): List<ItemDto>? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getItemList(token) // token 전달
        return response.body()?.data
    }

    // `token` 파라미터 추가
    suspend fun getEquippedItem(accessToken: String): ItemDto? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getEquippedItem(token) // token 전달
        return response.body()?.data
    }

    // `token` 파라미터 추가 및 `service` 사용으로 통일
    suspend fun changeEquippedItem(accessToken: String, itemId: Int): ApiResponse<ItemDto>? {
        // "Bearer " 접두사 추가 (서버 요구사항에 따라 다름)
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.changeEquippedItem(token, itemId) // token 전달
        return response.body()
    }
    // 아이템 획득
    suspend fun claimReward(accessToken: String): ItemDto? {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.claimReward(token)
        return response.body()?.data
    }

    //친구 보관함(아이템 리스트 조회)
    suspend fun getFriendItems(
        accessToken: String,
        friendUserId: String //조회할 친구 userId
    ): Pair<List<ItemDto>, Long?> {
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getFriendItems(token, friendUserId)

        // 서버 표준 응답의 data 파트: items(List<Long>), selectedItem(Long?)
        val data = response.body()?.data
        val List = data?.items ?: emptyList()
        val selected = data?.selectedItem

        // 실제 ItemDto 리스트로 변환
        val items: List<ItemDto> = List.map { pk ->
            ItemDto(
                itemId = pk.toInt(),
                name = mapItemIdToName(pk),
                level = 1,                       // 서버가 안 주므로 기본값
                isSelected = (pk == selected)  // 선택한 아이템인지 여부
            )
        }

        //변환된 아이템 리스트와 선택된 아이템 반환
        return items to selected
    }

    // 아이템 매핑
    private fun mapItemIdToName(id: Long): String = when (id.toInt()) {
        1  -> "tissue"
        2  -> "cup"
        3  -> "paper"
        4  -> "coal"
        5  -> "tier"
        6  -> "trash"
        7  -> "chopstick"
        8  -> "potato"
        9  -> "cheeze"
        10 -> "egg"
        11 -> "mashmellow"
        12 -> "meat"
        13 -> "chicken"
        14 -> "soup"
        15 -> "ruby"
        else -> "자물쇠"
    }
}
