package com.example.hwaroak.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hwaroak.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * 서비스로 등록해 FCM 토큰 만료 시 새로 서버에 보내주기
 * 또한, 푸시 알람이 올 시 이를 OS/휴대폰 단위에서 띄우기
 * **/

class PushMessage : FirebaseMessagingService() {
    //기존 토큰이 만료되어 새 토큰을 받아야 할 경우
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("log_fcm", "갱신된 FCM 토큰: $token")
        
        /**이 이후에 FCM 토큰을 보내는 API 설정**/
        
    }

    //백에서 firebase에게 메시지를 보넀을 시 받는 거
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //1. Notification 페이로드
        val title = message.notification?.title
        val body = message.notification?.body

        //2. Data 페이로드
        val data = message.data

    }

    private fun showNotification(
        title: String,
        body: String,
        contentIntent: PendingIntent
    ) {
        // 1) 알림을 구분할 채널 ID 정의
        val channelId = "friend_events"

        // 2) 시스템의 NotificationManager 얻기
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 3) Android 8.0(API 26) 이상에서는 “Notification Channel”을 반드시 만들어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 3-1) 채널 ID, 사용자에게 보일 채널 이름, 중요도를 지정
            val channel = NotificationChannel(
                channelId,                  // 채널 구분자
                "친구 알림",                // 설정 화면에 표시될 채널명
                NotificationManager.IMPORTANCE_HIGH  // 소리·팝업 등 알림 중요도
            )
            manager.createNotificationChannel(channel)
        }

        // 4) 알림 빌더 생성: 채널 ID를 함께 넘겨줘야 채널 설정을 따름
        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_emotion1) // 4-1) 상태바에 표시할 아이콘
            .setContentTitle(title)                   // 4-2) 알림 제목
            .setContentText(body)                     // 4-3) 알림 본문
            .setContentIntent(contentIntent)          // 4-4) 클릭 시 실행될 PendingIntent
            .setAutoCancel(true)                      // 4-5) 클릭 후 자동으로 알림 제거
            .build()                                  // 4-6) Notification 객체로 빌드

        // 5) 알림 발송: 고유 ID(여기선 timestamp)로 시스템에게 보여 달라고 요청
        manager.notify(System.currentTimeMillis().toInt(), notif)
    }

}