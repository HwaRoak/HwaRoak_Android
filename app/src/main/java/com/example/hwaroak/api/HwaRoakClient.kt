package com.example.hwaroak.api

import com.example.hwaroak.api.diary.network.DiaryService
import com.example.hwaroak.api.login.network.LoginService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HwaRoakClient {

    //Swagger에서 가져오는 주소
    private const val BASE_URL = "http://52.78.74.252/"

    //일단 호출을 로그로 추적하게
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    //클라이언트(각 연결/쓰기/읽기 작업에서 최대 60초 대기)
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    //각자 API service(network)를 구현하고 밑에 추가함으로써 유지보수 용이
    //로그인 관련
    val loginService: LoginService = retrofit.create(LoginService::class.java)
    //일기 관련
    val diaryService: DiaryService = retrofit.create(DiaryService::class.java)


}