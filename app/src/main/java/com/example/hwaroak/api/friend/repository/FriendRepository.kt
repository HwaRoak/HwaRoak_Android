package com.example.hwaroak.api.friend.repository

import android.util.Log
import com.example.hwaroak.api.HwaRoakClient.friendService
import com.example.hwaroak.api.diary.model.DiaryResponseBody
import com.example.hwaroak.api.friend.model.ApiResponse
import com.example.hwaroak.api.friend.model.FriendRequestResponse
import com.example.hwaroak.api.friend.model.FriendResponse
import com.example.hwaroak.api.friend.model.ReceivedFriendData
import com.example.hwaroak.api.friend.network.FriendService
import com.example.hwaroak.ui.friend.FriendData

class FriendRepository(private val api: FriendService) {

    //1. 친구 목록 조회
    suspend fun getFriendList(token: String): Result<List<FriendResponse>> {
        return try {
            val response = api.getFriendList("Bearer $token")
            Log.d("FriendRepository", "API 응답: ${response.body()}")
            if (response.isSuccessful) {
                val friendList = response.body()?.data ?: emptyList()
                Result.success(friendList)
            } else {
                Log.e("FriendRepository", "응답 실패: ${response.code()}, ${response.message()}")
                Result.failure(Exception("서버 응답 실패"))
            }
        } catch (e: Exception) {
            Log.e("FriendRepository", "예외 발생: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // 2. 친구 아이디 검색
    suspend fun searchFriend(token: String, userId: String): Result<FriendData> {
        return try {
            val response = api.searchFriend(token, userId)

            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data
                val friendData = FriendData(
                    name = data.nickname ?: "이름 없음",
                    id = data.userId,
                    isRequested = false
                )
                Result.success(friendData)
            } else {
                Result.failure(Exception("친구 검색 실패: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. 친구 요청 보내기
    suspend fun sendFriendRequest(token: String, receiverId: String): Result<Unit> {
        return try {
            val response = api.sendFriendRequest(token, receiverId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorCode = if (errorBody?.contains("FRIEND4004") == true) {
                    "FRIEND4004"
                } else {
                    "UNKNOWN"
                }
                Result.failure(Exception(errorCode))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. 친구 요청 목록
    suspend fun getReceivedFriendRequests(token: String): Result<List<ReceivedFriendData>> {
        return try {
            val response = api.getReceivedFriendRequests(token)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("받은 친구 요청 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. 친구 요청 수락
    suspend fun acceptFriendRequest(token: String, friendId: String): Result<Unit> {
        return try {
            val response = api.acceptFriendRequest("Bearer $token", friendId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("FriendRepository", "수락 실패: $errorBody")
                Result.failure(Exception("수락 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("FriendRepository", "예외 발생: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // 6. 친구 요청 거절
    suspend fun rejectFriendRequest(token: String, friendId: String): Result<Unit> {
        return try {
            val response = api.rejectFriendRequest("Bearer $token", friendId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val message = response.errorBody()?.string()
                Log.e("FriendRepository", "거절 실패: $message")
                Result.failure(Exception("거절 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 7. 친구 삭제(친구목록에서)
    suspend fun deleteFriend(token: String, friendId: String): Result<Unit> {
        return try {
            val response = api.deleteFriend("Bearer $token", friendId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                Result.failure(Exception("삭제 실패: $errorMessage"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}
