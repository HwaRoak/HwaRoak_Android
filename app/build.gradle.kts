import java.util.Properties
import java.io.FileInputStream


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //fragment에 직렬화를 위해
    id("kotlin-parcelize")
    //구글 서비스 플러그인 적용
    id("com.google.gms.google-services")
    //Glide를 위해
    id("kotlin-kapt")
}

android {
    namespace = "com.example.hwaroak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hwaroak"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //API를 넣는 코드
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
        buildConfigField("String", "BASE_URL_SSE", "\"${properties.getProperty("BASE_URL_SSE")}\"")

    }

    buildFeatures {
        // ViewBinding 활성화
        viewBinding = true
        // Gradile이 BuildConfig을 하게
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // 1) HTTP 클라이언트 & JSON 파싱
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")


    // 3) Material Components (DatePicker 포함)
    implementation("com.google.android.material:material:1.9.0")

    // 4) bottomnavigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")
    implementation("com.google.android.material:material:1.11.0")
    
    // 5) 기타 기능 및 위젯들 implement

    // 5) kakao login
    implementation("com.kakao.sdk:v2-user:2.13.0")
    implementation("com.kakao.sdk:v2-auth:2.13.0")

    //6) splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    //7) grid layout
    implementation("androidx.gridlayout:gridlayout:1.0.0")

    //달력
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    // ThreeTenABP: Android용 Backport
    implementation("com.jakewharton.threetenabp:threetenabp:1.3.1")

    //원형 그래프(PieChart)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx:22.5.0")

    // Cloud Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")

    //Http-SSE 통신
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")

    //프로필 이미지 관련 Glide 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    //프로필 이미지를 둥글게 만들어 주는 라이브러리
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // …기존 의존성들…

}