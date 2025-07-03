package com.example.hwaroak.ui.login

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "dda043b8f767e0cc16ee81ad26e1fca1")
    }
}