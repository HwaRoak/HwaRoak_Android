package com.example.hwaroak.ui.main

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ActivityMainBinding
import com.example.hwaroak.ui.calendar.CalendarFragment
import com.example.hwaroak.ui.diary.DiaryFragment
import com.example.hwaroak.ui.friend.FriendFragment
import com.example.hwaroak.ui.mypage.MypageFragment
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //splash 화면 테마 되돌리기
        setTheme(R.style.Theme_HwaRoak)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //키해시값 추출(자유롭게 삭제가능)
        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)//해시값: qEz+AlHISa6EGnodqhzTq6Me1hU=

        //맨 처음 화면을 보여줄 때, HomeFragment Default
        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, HomeFragment())
                .commit()
        }

        //BottomNavigationView 연결
        binding.mainBnv.setOnItemSelectedListener { item ->
            when (item.itemId){

                //매인 화면
                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, HomeFragment())
                        .commit()
                    true
                }

                //일기 작성 화면
                R.id.diaryFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, DiaryFragment())
                        .commit()
                    true
                }

                //일기 히스토리 화면
                R.id.calendarFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, CalendarFragment())
                        .commit()
                    true
                }

                //친구 화면
                R.id.friendFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, FriendFragment())
                        .commit()
                    true
                }

                //마이페이지 화면
                R.id.mypageFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, MypageFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}