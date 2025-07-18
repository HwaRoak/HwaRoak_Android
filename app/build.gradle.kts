plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //fragment에 직렬화를 위해
    id("kotlin-parcelize") 
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
    }

    buildFeatures {
        // ViewBinding 활성화
        viewBinding = true
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
}