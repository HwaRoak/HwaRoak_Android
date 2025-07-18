package com.example.hwaroak.api.login.model

//Access token을 보내는 data
data class LoginRequest(
    val accessToken : String
)

//JWT 토큰을 재발급 받을 때 보내는 data
data class TokenGetRequest(
    val accessToken: String,
    val refreshToken: String
)



//API 결과를 받는 data
//실제 사용 시 LoginResponse<MemberData>
data class LoginResponse<T>(
    val status : String,
    val code : String,
    val data : T? = null,
    val message : String? = null
)
data class MemberData(
    val memberId : Int,
    val nickname : String,
    val accessToken: String,
    val refreshToken: String
)

//JWT 토큰 수신 body
data class TokenGetResponse<T>(
    val status : String,
    val code : String,
    val data : T? =null,
)
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)
