package com.example.hwaroak.message
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.hwaroak.BuildConfig
import com.example.hwaroak.R
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources

//알람 파싱 데이터 클래스
data class AlarmEvent(
    val id: Int,
    val receiverId: Int,
    val title: String,
    val alarmType: String,
    val message: String,
    val createdAt: String
)


class SSEClient(private val context: Context) {


    //eventSoure를 변수로 뽑아
    private var eventSource: EventSource? = null

    //유저 설정(무한 대기)
    private val client =  OkHttpClient.Builder()
        .readTimeout(0, java.util.concurrent.TimeUnit.MILLISECONDS) // 무한 대기
        .build()


    val prefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val accessToken = prefs.getString("accessToken", "") ?: ""
    val token = if (accessToken.startsWith("Bearer ")) accessToken else "Bearer $accessToken"

    //연결 로직
    fun connectToSSE(){
        val request = Request.Builder()
            .url(BuildConfig.BASE_URL_SSE)
            .addHeader("Authorization", token)
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
                Log.d("log_SSE", "도착: $data")
                if(data.toString() != "subscribe") {
                    try {
                        val alarmEvent = Gson().fromJson(data, AlarmEvent::class.java)

                        // createdAt → "2025-07-29T20:35:51.652606127" → "20:35" 추출
                        val time = alarmEvent.createdAt.substringAfter("T").substring(0, 5)

                        val title = alarmEvent.title
                        val message = alarmEvent.message

                        showNotification(title, message)

                    } catch (e: Exception) {
                        Log.e("log_SSE", "파싱 실패: $e")
                    }
                }

            }

            override fun onClosed(eventSource: EventSource) {
                super.onClosed(eventSource)
                Log.d("log_SSE", "SSE 연결 종료됨")
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                super.onFailure(eventSource, t, response)
                Log.d("log_SSE", "SSE 연결 실패 : $t")

                //재연결
                Handler(Looper.getMainLooper()).postDelayed({
                    connectToSSE()
                }, 1000)

            }
        }

        //이제 EventSource 객체를 만들어서 위에 정의한 client랑 listenr 내용을 넣기
        //request : 연결 주소
        //listener : 연결 중 발생하는 이벤트에 대한 콜백 정의
        eventSource = EventSources.createFactory(client)
            .newEventSource(request, listener)
    }

    //이를 바탕으로 팝업 띄우기
    fun showNotification(title: String, message: String){
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
            .setSmallIcon(R.drawable.ic_app_hwaroak_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
        Log.d("log_SSE", "SSE notification check")
    }

    fun disconnect() {
        eventSource?.cancel() // EventSource 객체가 null이 아니면 cancel() 호출
        eventSource = null // 가비지 컬렉션(Garbage Collection)을 위해 null로 설정
    }


}