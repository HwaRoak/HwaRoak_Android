package com.example.hwaroak.api.login.model



//1. 카카오 로그인
data class LoginRequest(
    val accessToken : String
)
data class LoginResponse<T>(
    val status : String,
    val code : String,
    val message : String,
    val data : T? = null
)
data class MemberData(
    val accessToken: String,
    val refreshToken: String,
    val memberId : Int,
    val nickname : String
)


//2. JWT 토큰을 재발급 받을 때 보내는 data
data class TokenGetRequest(
    val accessToken: String,
    val refreshToken: String
)

/**Response**/
data class TokenGetResponse<T>(
    val status : String,
    val code : String,
    val message: String,
    val data : T? =null,
)
data class TokenData(
    val accessToken: String,
    val refreshToken: String
)
