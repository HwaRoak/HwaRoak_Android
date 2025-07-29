package com.example.hwaroak.api.login.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.login.model.LoginRequest
import com.example.hwaroak.api.login.model.TokenGetRequest
import com.example.hwaroak.api.login.network.LoginService

class LoginRepository(private val service: LoginService,
                      private val pref: SharedPreferences) {

    //카카오 로그인
    suspend fun KakaoLogin(accessToken: String) : Boolean {
        try {
            val response = service.kakaoLogin(LoginRequest(accessToken))

            //응답이 유효하면
            //if(response.isSuccessful && response.body()?.code == "SUCCESS"){
            if(response.isSuccessful){
                //인자로 받은 sharedPref를 IN
                response.body()!!.data!!.let { memberData ->
                    //liam
                    HwaRoakClient.currentAccessToken = memberData.accessToken
                    //
                    pref.edit{
                        putInt("memberId", memberData.memberId)
                        putString("nickname", memberData.nickname)
                        putString("accessToken", memberData.accessToken)
                        putString("refreshToken", memberData.refreshToken)
                    }
                }
                return true
            }
            //유효하지 않으면 false 리턴
            else{
                return false
            }

        } catch (e : Exception){
            Log.d("kakaoLogin", e.toString())
            return false
        }
    }

    suspend fun requestToken(accessToken: String, refreshToken: String) : Boolean {
        try {
            val response = service.requestToken(TokenGetRequest(accessToken, refreshToken))
            if(response.isSuccessful){
                response.body()!!.data!!.let { tokenData ->
                    pref.edit{
                        putString("accessToken", tokenData.accessToken)
                        putString("refreshToken", tokenData.refreshToken)
                    }
                }
                return true
            }
            else{
                return false
            }
        }
        catch (e : Exception) {
            Log.d("kakaoLogin", e.toString())
            return false
        }
    }

}