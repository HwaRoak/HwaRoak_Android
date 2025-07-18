package com.example.hwaroak.api.diary.repository

import com.example.hwaroak.api.diary.model.DiaryRequest
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
    suspend fun writeDiary(token: String, req: DiaryRequest): Result<String> {
        return try {
            //response는 DiaryResponse형 객체로 API 호출 결과를 받는다. (API 명세서 참고)
            val response = service.writeDiary("Bearer $token", req)
            //유효성 검사
            if(response.isSuccessful && response.body()?.code == "SUCCESS"){
                Result.success(response.body()!!.data ?: "성공")
            }
            else{
                Result.failure(
                    Exception(response.body()?.message ?: "실패")
                )
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }


}