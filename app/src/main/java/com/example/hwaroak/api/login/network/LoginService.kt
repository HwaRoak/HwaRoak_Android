package com.example.hwaroak.api.login.network

import com.example.hwaroak.api.login.model.LoginRequest
import com.example.hwaroak.api.login.model.LoginResponse
import com.example.hwaroak.api.login.model.LoginSuccess
import com.example.hwaroak.api.login.model.MemberData
import com.example.hwaroak.api.login.model.TokenData
import com.example.hwaroak.api.login.model.TokenGetRequest
import com.example.hwaroak.api.login.model.TokenGetResponse
import com.example.hwaroak.api.login.model.TokenGetSucccess
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    //로그인 (토큰 받기)
    @POST("api/v1/auth/kakao")
    suspend fun kakaoLogin(
        @Body req: LoginRequest
    ) : Response<LoginSuccess>

    //Response<LoginResponse<MemberData>>

    //토큰 재요청
    @POST("api/v1/auth/reissue")
    suspend fun requestToken(
        @Body req: TokenGetRequest
    ) : Response<TokenGetSucccess>

    //Response<TokenGetResponse<TokenData>>
}