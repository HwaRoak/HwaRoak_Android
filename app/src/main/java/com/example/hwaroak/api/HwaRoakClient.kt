package com.example.hwaroak.api

import com.example.hwaroak.api.diary.network.DiaryService
import com.example.hwaroak.api.friend.network.FriendService
import com.example.hwaroak.api.home.network.ItemApiService
import com.example.hwaroak.api.login.network.LoginService
import com.example.hwaroak.api.notice.network.NoticeService
import okhttp3.Interceptor
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

    // Liam
    // TODO: 실제로 앱에서 토큰을 저장하고 가져오는 로직으로 대체해야 합니다.
    // 여기서는 임시로 토큰을 저장할 변수를 선언합니다.
    // 실제 앱에서는 SharedPreferences나 다른 보안 저장소에 저장해야 합니다.
    @Volatile // 여러 스레드에서 접근할 수 있으므로 volatile 키워드 추가
    var currentAccessToken: String? = null // 로그인 성공 후 여기에 토큰 저장

    // 인증 헤더를 추가하는 인터셉터
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        // currentAccessToken이 null이 아니면 Authorization 헤더 추가
        currentAccessToken?.let { token ->
            builder.header("Authorization", "Bearer $token")
        }

        val request = builder.build()
        chain.proceed(request)
    }

    //클라이언트(각 연결/쓰기/읽기 작업에서 최대 60초 대기)
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(authInterceptor) // **인증 인터셉터 추가**
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
    //아이템 관련
    val itemApiService: ItemApiService by lazy { // ItemApiService를 사용하도록 변경
        retrofit.create(ItemApiService::class.java)
    }
    //친구 관련
    val friendService: FriendService = retrofit.create(FriendService::class.java)
    //알람 관련
    val noticeService: NoticeService = retrofit.create(NoticeService::class.java)



}