package com.example.hwaroak.ui.login

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hwaroak.databinding.ActivityLoginKakaoBinding
import com.example.hwaroak.ui.main.MainActivity
import com.kakao.sdk.auth.TokenManagerProvider
//import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginKakaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginKakaoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginKakaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //kakaoLogout()

        //sharedPrefence
        val agreePref = getSharedPreferences("agree", MODE_PRIVATE)
        val checkAgree = agreePref.getBoolean("agree", false)

        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            }
            else if (tokenInfo != null) {
                // 토큰 정보 보기
                Log.i("KakaoLogin", "accessTokenInfo 성공, 회원번호: ${tokenInfo.id}, 만료시간: ${tokenInfo.expiresIn}")
                // 토큰 그대로 꺼내서 로그 찍기
                val token = TokenManagerProvider
                    .instance
                    .manager
                    .getToken()
                    ?.accessToken
                Log.i("KakaoLogin", "실제 AccessToken: $token")


                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()

                //약관 동의를 했으면 
                if(checkAgree) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
                //약관 동의를 안했으면
                else{
                    val intent = Intent(this, AgreeActivity::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }
            }
        }

        // val keyHash = Utility.getKeyHash(this)
        // Log.d("Hash", keyHash)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                //
                Log.i("KakaoLogin", "로그인 성공: ${token.accessToken}")
                //
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }

        val kakao_login_button = binding.loginBtn // 로그인 버튼

        kakao_login_button.setOnClickListener {
            val activityContext = this@LoginKakaoActivity

            val scopes = listOf(
                "profile_nickname",
                "profile_image",
                // 이메일이 필요하면 비즈앱 전환 후에만 사용 가능
                // "account_email"
            )


            if(UserApiClient.instance.isKakaoTalkLoginAvailable(activityContext)){
                UserApiClient.instance.loginWithKakaoTalk(
                    activityContext,
                    //scopes = scopes,
                    callback = callback)
            }else{
                UserApiClient.instance.loginWithKakaoAccount(
                    activityContext,
                    //scopes = scopes,
                    callback = callback)
            }

        }
    }

    private fun kakaoLogout() {
        // 1) 카카오 서버에 로그아웃 요청
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("KakaoLogout", "로그아웃 실패", error)
            } else {
                Log.i("KakaoLogout", "로그아웃 성공")
            }
            // 2) 로컬에 저장된 토큰(액세스/리프레시) 완전 삭제
            TokenManagerProvider
                .instance
                .manager
                .clear()
        }
    }

}