package com.example.hwaroak.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.login.repository.LoginRepository
import com.example.hwaroak.databinding.ActivityLoginKakaoBinding
import com.example.hwaroak.message.PushMessage
import com.example.hwaroak.ui.main.MainActivity
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.TokenManagerProvider
//import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class LoginKakaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginKakaoBinding

    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var diaryPref: SharedPreferences
    //로그인 API 일루와잇
    private lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginKakaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Android의 버전이 높은 경우 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }



        //sharedPreference 정의(user) 및 repo 정의
        pref = getSharedPreferences("user", MODE_PRIVATE)
        loginRepository = LoginRepository(HwaRoakClient.loginService, pref)

        //일기 정보 체크
        diaryPref = getSharedPreferences("diary", MODE_PRIVATE)

        //kakaoLogout()
        //FCM 토큰 get
        getFCMToken()

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


                //Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()

                /**API 불러오기**/
                getAccessTokenWithLogin(token!!, checkAgree)


            }
        }

        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

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
                getAccessTokenWithLogin(token.accessToken, checkAgree)
            }
        }

        val kakao_login_button = binding.loginBtn // 로그인 버튼

        kakao_login_button.setOnClickListener {
            val activityContext = this@LoginKakaoActivity

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


        //일기 정보 체크(만약 오늘과 다르면 false)
        val recordDate = diaryPref.getString("recordDate", "") ?: ""
        val tmp1 = diaryPref.getBoolean("isWrite", false)
        val tmp2 = diaryPref.getInt("reward", 0)
        Log.d("log_diary", "--일기 작성 기록 체크--")
        if(!isEquailToday(recordDate)){
            Log.d("log_diary", "--날짜가 다르므로 pref를 초기화합니다--")
            diaryPref.edit{
                putBoolean("isWrite", false)
                putInt("reward", 0)
                putString("memberItemName", "")
                putInt("diaryId", 0)
                putInt("barType", 0)
                putString("recordDate", "")
                apply()
            }
        }

    }

    //카카오 토큰을 이용해 API 호출 
    private fun getAccessTokenWithLogin(token: String, checkAgree: Boolean){
        //API는 무조건 lifeScope로 돌리기
        lifecycleScope.launch {
            val ok = loginRepository.KakaoLogin(token)
            withContext(Dispatchers.Main){
                //엑세스 토큰 발급 받은 경우
                //성공적!
                if(ok){
                    Toast.makeText(this@LoginKakaoActivity, "API 받아오기 성공!", Toast.LENGTH_SHORT).show()
                    Log.d("kakaoLogin", "엑세스 토큰: " + pref.getString("accessToken", "").toString())
                    Log.d("kakaoLogin", "리프레시 토큰: " + pref.getString("refreshToken", "").toString())
                    Log.d("kakaoLogin", "닉네임: " + pref.getString("nickname", "").toString())
                    Log.d("kakaoLogin", "유저 아이디: " + pref.getInt("memberId", 0).toString())
                    /**실험**/
                    //val ok2 = loginRepository.requestToken(pref.getString("accessToken", "").toString(),
                    //    pref.getString("refreshToken", "").toString())
                    //Log.d("kakaoLogin", "재발금 후 엑세스 토큰: " + pref.getString("accessToken", "").toString())
                    //Log.d("kakaoLogin", "재발금 후 리프레시 토큰: " + pref.getString("refreshToken", "").toString())

                    //약관 동의를 했으면
                    if(checkAgree) {
                        val intent = Intent(this@LoginKakaoActivity, MainActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                    //약관 동의를 안했으면
                    else{
                        val intent = Intent(this@LoginKakaoActivity, AgreeActivity::class.java)
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        finish()
                    }
                }
                //실패적!
                else{
                    Toast.makeText(this@LoginKakaoActivity, "API 받아오기 실패!", Toast.LENGTH_SHORT).show()
                }
            }
        } //lifescope 끝
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

    //오늘 날짜랑 비교 준비(sharedPref 초기화 용도)
    fun isEquailToday(dateString: String): Boolean {
        // 1) 포맷터 준비 (Asia/Seoul)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).apply {
            timeZone = TimeZone.getTimeZone("Asia/Seoul")
        }

        val todayString = sdf.format(Date())
        Log.d("log_diary", "$todayString / $dateString")
        // 2) 비교
        return dateString == todayString
    }

    //Firebase를 이용해 FCM 토큰을 받아오기
    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->  
                //토큰 get 실패 시
                if(!task.isSuccessful){
                    Log.d("log_fcm", "FCM 토큰 얻기에 실패했습니다.")
                    return@addOnCompleteListener
                }
                val token = task.result!!
                Log.d("log_fcm", "FCM 토큰: $token")
                
                /**차후 API로 토큰을 보내주기**/
                
            }

    }

}