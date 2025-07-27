package com.example.hwaroak.api.home.repository

import com.example.hwaroak.api.home.model.ApiResponse
import com.example.hwaroak.api.home.model.ItemDto
import com.example.hwaroak.api.home.network.ItemService

class ItemRepository(private val service: ItemService) {

    suspend fun getItems(): List<ItemDto>? {
        val response = service.getItemList()
        return response.body()?.data
    }

    suspend fun getEquippedItem(): ItemDto? {
        val response = service.getEquippedItem()
        return response.body()?.data
    }

    suspend fun changeEquippedItem(itemId: Int): ApiResponse<ItemDto>? {
        val response = service.selectItem(itemId)
        return response.body()
    }
}
