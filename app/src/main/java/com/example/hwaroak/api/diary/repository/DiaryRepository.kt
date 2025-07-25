package com.example.hwaroak.api.diary.repository

import com.example.hwaroak.api.diary.model.DiaryDetailResponse
import com.example.hwaroak.api.diary.model.DiaryEditRequest
import com.example.hwaroak.api.diary.model.DiaryEditResponse
import com.example.hwaroak.api.diary.model.DiaryLookResponse
import com.example.hwaroak.api.diary.model.DiaryMonthResponse
import com.example.hwaroak.api.diary.model.DiaryWriteRequest
import com.example.hwaroak.api.diary.model.DiaryWriteResponse
import com.example.hwaroak.api.diary.network.DiaryService
import java.io.IOException
import retrofit2.HttpException


/**
 * repository
 * API 통신을 담당하는 클래스로 network에서 작성한 인터페이스를 바탕으로
 * API 호출의 역할을 맡는다.
 * 실제 데이터 처리 및 UI 담당은 ViewModel에서 처리한다. (ViewModel을 이용한 MVVM 구조)
 *
 * 즉 DiaryRepository가 API를 호출하고 그 결과를 ViewModel로 전달
 * 
 * suspend : 코루틴 함수(일시 중단 가능) -> scope.launch 등과 같은 곳에서 사용 가능
 * 리턴 형식 : Result<T> 형태로  -> result.onsuccess / result.onfailure 를 통해 분리하여
 *           처리가 가능하다.
 * **/

class DiaryRepository(private val service: DiaryService) {

    //1. 일기 작성
    suspend fun writeDiary(
        accessToken: String, req: DiaryWriteRequest
    ) : Result<DiaryWriteResponse> =
        try{
            val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
            val response = service.writeDiary(token, req)
            //성공 리턴
            if(response.isSuccessful){
                val body = response.body()
                //Response body가 없을 때
                if(body == null){
                    Result.failure(RuntimeException("Response body is null"))
                }
                //data 값이 없을 때
                else if(body.data == null){
                    Result.failure(RuntimeException("Response OK but Data is null"))
                }
                else{
                    Result.success(body.data)
                }

            }
            //잘못된 리턴(200X 오류 등)
            else{
                val errMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
            }
        } catch (e: Exception){
            //오류
            Result.failure(e)
        }

    //2. 일기 수정
    suspend fun editDiary(accessToken: String, diaryID: Int, req: DiaryEditRequest)
    :Result<DiaryEditResponse> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.editDiary(token, diaryID, req)
        if(response.isSuccessful){
            val body = response.body()
            //Response body가 없을 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else{
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }

    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //3. 일기 삭제
    suspend fun deleteDiary(accessToken: String, diaryID: Int) : Result<Unit> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.deleteDiary(token, diaryID)
        if(response.isSuccessful){
            Result.success(Unit)
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //4. 월별 일기 조회
    suspend fun getMonthDiary(accessToken: String, year: Int, month: Int)
    : Result<List<DiaryMonthResponse>> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getMonthDiary(token, year, month)
        if(response.isSuccessful){
            val body = response.body()
            //body가 null일 때
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            //data 값이 없을 때
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else {
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }

    }   catch (e: Exception){
        //오류
        Result.failure(e)
    }

    //5. 날짜별 일기 조회(피드백)
    suspend fun getDiary(accessToken: String, date: String)
    : Result<DiaryLookResponse> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getDiary(token, date)
        if(response.isSuccessful){
            val body = response.body()
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else{
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류 리턴
        Result.failure(e)
    }

    //6. 일기 상세 조회(일기
    suspend fun getDetailDiary(accessToken: String, diaryID: Int)
    :Result<DiaryDetailResponse> = try{
        val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"
        val response = service.getDetailDiary(token, diaryID)
        if(response.isSuccessful){
            val body = response.body()
            if(body == null){
                Result.failure(RuntimeException("Response body is null"))
            }
            else if(body.data == null){
                Result.failure(RuntimeException("Response OK but Data is null"))
            }
            else{
                Result.success(body.data)
            }
        }
        else{
            val errMsg = response.errorBody()?.string() ?: response.message()
            Result.failure(RuntimeException("HTTP ${response.code()}: $errMsg"))
        }
    } catch (e: Exception){
        //오류 리턴
        Result.failure(e)
    }

}