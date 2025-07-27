package com.example.hwaroak.message
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hwaroak.R
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

class SSEClient(private val context: Context) {

    private val client = OkHttpClient()

    fun connectToSSE(){
        val request = Request.Builder()
            .url("http://....")
            .build()

        val listener = object : EventSourceListener() {
            //맨 처음 연결했을 때
            override fun onOpen(eventSource: EventSource, response: Response) {
                // 연결이 열렸을 때의 처리
                Log.d("log_SSE", "SSE 연결됨")
            }

            //이벤트가 발생했을 때 (단방향 데이터가 왔을 때)
            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                super.onEvent(eventSource, id, type, data)
                /**처리하기**/
                showNotification(data)
            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                Log.d("log_SSE", "SSE 연결 종료됨")
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Log.d("log_SSE", "SSE 연결 실패 : $t")
            }
        }

        //이제 EventSource 객체를 만들어서 위에 정의한 client랑 listenr 내용을 넣기
        //request : 연결 주소
        //listener : 연결 중 발생하는 이벤트에 대한 콜백 정의
        EventSources.createFactory(client)
            .newEventSource(request, listener)
    }

    fun showNotification(message: String){
        //notification Manager 세팅
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sse_channel"

        //API 26이상에서는 사전에 채널을 등록해야 함.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, "SSE 알림", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        //알람 세팅
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_emotion1)
            .setContentTitle("테스트 알림입니다.")
            .setContentText("테스트 내용입니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("log_SSE", "SSE notification check")
    }


}