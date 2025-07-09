package com.example.hwaroak.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ActivityMainBinding
import com.example.hwaroak.ui.calendar.CalendarFragment
import com.example.hwaroak.ui.diary.DiaryFragment
import com.example.hwaroak.ui.friend.FriendFragment
import com.example.hwaroak.ui.locker.LockerFragment
import com.example.hwaroak.ui.mypage.MypageFragment
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ThreeTenABP 초기화(애플리케이션 상 1번)
        com.jakewharton.threetenabp.AndroidThreeTen.init(applicationContext)

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
                    binding.mainBnv.visibility = ConstraintLayout.VISIBLE
                    true
                }

                //일기 작성 화면
                R.id.diaryFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, DiaryFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.GONE
                    true
                }

                //일기 히스토리 화면
                R.id.calendarFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, CalendarFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.VISIBLE
                    true
                }

                //친구 화면
                R.id.friendFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, FriendFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.VISIBLE
                    true
                }

                //마이페이지 화면
                R.id.mypageFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, MypageFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.VISIBLE
                    true
                }
                else -> false
            }
        }

        //상단바 연결(보관함, 알림창)
        //보관함
        binding.mainLockerBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, LockerFragment())
                .commit()
            // BottomNavigationView는 보이게 설정
            binding.mainBnv.visibility = View.VISIBLE
        }
        //알림창(추후 구현)


        //뒤로 가기 시 일단 HomeFragment 이동 후 종료
        handleBack()



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }


    //bottomNavView에서 item ID 입력 시 해당 id에 해당하는 fragment로 이동
    fun selectTab(@IdRes menuItemId: Int) {
        binding.mainBnv.selectedItemId = menuItemId
    }


    //뒤로 가기 제어
    private fun handleBack(){

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 현재 화면 확인
                val current = supportFragmentManager
                    .findFragmentById(R.id.main_fragmentContainer)
                if (current !is HomeFragment) {
                    // 홈으로 돌아가기
                    binding.mainBnv.selectedItemId = R.id.homeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, HomeFragment())
                        .commit()
                } else {
                    // 홈 화면일 땐 기본 뒤로가기 동작(앱 종료)
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

    }

}