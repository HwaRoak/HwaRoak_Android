package com.example.hwaroak.api.login.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.hwaroak.api.login.model.LoginRequest
import com.example.hwaroak.api.login.network.LoginService

class LoginRepository(private val service: LoginService,
                      private val pref: SharedPreferences) {

    //카카오 로그인
    suspend fun KakaoLogin(accessToken: String) : Boolean {
        try {
            val response = service.kakaoLogin(LoginRequest(accessToken))
            //응답이 유효하면
            if(response.isSuccessful && response.body()?.code == "SUCCESS"){
                //인자로 받은 sharedPref를 IN
                response.body()!!.data?.let { memberData ->
                    pref.edit{
                        putInt("memberId", memberData.memberId)
                        putString("nickname", memberData.nickname)
                    }
                }
                return true
            }
            //유효하지 않으면 false 리턴
            else{
                return false
            }

        } catch (e : Exception){
            return false
        }
    }

    suspend fun requestToken(accessToken: String, refreshToken: String){

    }

}