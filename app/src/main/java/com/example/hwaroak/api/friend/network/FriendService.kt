package com.example.hwaroak.api.friend.network

import com.example.hwaroak.api.friend.model.ApiResponse
import com.example.hwaroak.api.friend.model.FriendRequestResponse
import com.example.hwaroak.api.friend.model.FriendResponse
import com.example.hwaroak.api.friend.model.FriendSearchResponse
import com.example.hwaroak.api.friend.model.ReceivedFriendResponse
import com.example.hwaroak.ui.friend.FriendData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendService {
    //친구 목록 조회
    @GET("/api/v1/friends")
    suspend fun getFriendList(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<FriendResponse>>>

    //친구 검색
    @GET("/api/v1/friends/search/{userId}")
    suspend fun searchFriend(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<FriendSearchResponse>>

    //친구 요청 보내기
    @POST("/api/v1/friends/request/{userId}")
    suspend fun sendFriendRequest(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<FriendRequestResponse>

    //친구 요청 목록
    @GET("/api/v1/friends/received")
    suspend fun getReceivedFriendRequests(
        @Header("Authorization") token: String
    ): Response<ReceivedFriendResponse>

    //친구 요청 수락
    @POST("/api/v1/friends/{friendId}/accept")
    suspend fun acceptFriendRequest(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: String
    ): Response<Unit>

    //친구 요청 거절
    @POST("/api/v1/friends/{friendId}/reject")
    suspend fun rejectFriendRequest(
        @Header("Authorization") token: String,
        @Path("friendId") friendId: String
    ): Response<Unit>

    //친구 삭제(목록에서)
    @DELETE("/api/v1/friends/{userId}")
    suspend fun deleteFriend(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<Unit>>
}

