package com.example.hwaroak.ui.main

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hwaroak.R
import com.example.hwaroak.api.friend.access.FriendNavViewModel
import com.example.hwaroak.databinding.ActivityMainBinding
import com.example.hwaroak.message.SSEClient
import com.example.hwaroak.ui.calendar.CalendarFragment
import com.example.hwaroak.ui.diary.DiaryFragment
import com.example.hwaroak.ui.friend.FriendFragment
import com.example.hwaroak.ui.locker.LockerFragment
import com.example.hwaroak.ui.mypage.MypageFragment
import com.example.hwaroak.ui.mypage.SettingFragment
import com.example.hwaroak.ui.notification.NoticeFragment
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var pref: SharedPreferences
    private lateinit var title: String

    private val friendNavViewModel: FriendNavViewModel by viewModels()

    //호출을 위한 SSEClient
    private lateinit var sseClient: SSEClient


    // FriendVisitFragment에서 호출할 메서드
    private var currentFriendId: String? = null
    private var currentFriendName: String? = null

    override fun onStart() {
        super.onStart()
        sseClient.connectToSSE()
        Log.d("log_SSE", "MainActivity onStart() - SSE 연결 시작")
    }

    override fun onStop() {
        super.onStop()
        sseClient.disconnect()
        Log.d("log_SSE", "MainActivity onStop() - SSE 연결 종료")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //ThreeTenABP 초기화(애플리케이션 상 1번)
        com.jakewharton.threetenabp.AndroidThreeTen.init(applicationContext)

        //splash 화면 테마 되돌리기
        setTheme(R.style.Theme_HwaRoak)
        pref = getSharedPreferences("user", MODE_PRIVATE)
        val name = pref.getString("nickname", "") ?: ""
        val nickname = pref.getString("cachedNickname", "") ?: ""

        title = if(nickname == "") "${name}의 화록" else "${nickname}의 화록"
        binding.mainTitleTv.text = title


        //sse 연결
        sseClient = SSEClient(this)



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
                    clearFriendLockerContext() //친구 보관함 모드 해제
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, HomeFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.VISIBLE
                    showMainTopBar()
                    true
                }

                //일기 작성 화면
                R.id.diaryFragment -> {
                    clearFriendLockerContext() //친구 보관함 모드 해제
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_fragmentContainer, DiaryFragment())
                        .commit()
                    binding.mainBnv.visibility = ConstraintLayout.GONE
                    true
                }

                //일기 히스토리 화면
                R.id.calendarFragment -> {
                    clearFriendLockerContext()  //친구 보관함 모드 해제
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
                    clearFriendLockerContext() //친구 보관함 모드 해제
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
            binding.mainLockerBtn.setOnClickListener {
                //최상단이 이미 Locker면 무시 (중복 쌓임 방지)
                val top = supportFragmentManager.findFragmentById(R.id.main_fragmentContainer)
                if (top is LockerFragment) return@setOnClickListener

                //디바운스
                binding.mainLockerBtn.isEnabled = false

                val fid = currentFriendId
                val dest = if (fid.isNullOrBlank()) {
                    LockerFragment() //내 보관함
                } else {
                    LockerFragment().apply {
                        arguments = Bundle().apply {
                            putString("friendId", fid)
                            putString("friendNickname", currentFriendName)
                        } //친구 보관함
                    }
                }

                //화면 전환
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragmentContainer, dest)
                    .addToBackStack(null)   // 뒤로가기(또는 X)로 닫히길 원하면 유지
                    .commit()

                binding.mainBnv.visibility = View.VISIBLE
                binding.mainLockerBtn.postDelayed({ binding.mainLockerBtn.isEnabled = true }, 300)
            }
        }
        //알림창
        binding.mainBellBtn.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, NoticeFragment())
                .addToBackStack(null)
                .commit()
            // NoticeFragment로 갈 때 상단 바 숨김(모양이 다르므로 fragment에서 교체)
            //hideMainTopBar()// 상단 바 숨김
            // BottomNavigationView는 보이게 설정
            binding.mainBnv.visibility = View.VISIBLE
        }
        //뒤로 가기 < 버튼
        binding.mainBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            /*
            // 현재 화면 확인
            val current = supportFragmentManager
                .findFragmentById(R.id.main_fragmentContainer)

            // 1. 백스택에 프래그먼트가 존재하면 pop (예: MyPage → EditProfile → 뒤로 → MyPage)
            if (supportFragmentManager.backStackEntryCount > 0) {
                Log.d("log_back", "1111")
                supportFragmentManager.popBackStack()
                
            }
            // 2. 홈 화면으로 돌아가게 하기
            else if (current !is HomeFragment) {
                // 홈으로 돌아가기
                Log.d("log_back", "2222")
                binding.mainBnv.selectedItemId = R.id.homeFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragmentContainer, HomeFragment())
                    .commit()
            }
            else {
                // 홈 화면일 땐 기본 뒤로가기 동작(앱 종료)
                onBackPressedDispatcher.onBackPressed()
                Log.d("log_back", "3333")
            }
            */

        }


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

    //친구 요청 페이지로 이동 (bottomNav이동 -> bundle에 인자 담아 FriendFragment 이동 -> freind에서 UI 처리)
    fun navigateToFriendRequestPage() {
        // BottomNavigationView의 탭을 '친구' 탭으로 선택
        binding.mainBnv.selectedItemId = R.id.friendFragment

        // FriendFragment의 newInstance를 호출하여 인수를 전달
        val friendFragment = FriendFragment.newInstance(showRequests = true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragmentContainer, friendFragment)
            .commit()
    }

    //강제로 일기 작성 탭으로 이동하게 하기(단 bundle에 내용 채워서)
    fun navigateToDiaryWith(bundle: Bundle) {
        // 1. programmatically select the Diary tab
        binding.mainBnv.selectedItemId = R.id.diaryFragment
        // 2. create & commit your DiaryFragment with args
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragmentContainer, DiaryFragment().apply {
                arguments = bundle
            })
            .commit()
    }

    //뒤로 가기 제어
    private fun handleBack(){

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 현재 화면 확인
                val current = supportFragmentManager
                    .findFragmentById(R.id.main_fragmentContainer)

                // 1. 백스택에 프래그먼트가 존재하면 pop (예: MyPage → EditProfile → 뒤로 → MyPage)
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                    return
                }

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

    // 상단 바를 숨기는 함수
    fun hideMainTopBar() {
        binding.mainBackBtn.visibility = View.GONE
        binding.mainBellBtn.visibility = View.GONE
        binding.mainLockerBtn.visibility = View.GONE
        binding.mainTitleTv.visibility = View.GONE
    }

    // 상단 바를 다시 보이게 하는 함수
    fun showMainTopBar() {
        binding.mainBellBtn.visibility = View.VISIBLE
        binding.mainLockerBtn.visibility = View.VISIBLE
        binding.mainTitleTv.visibility = View.VISIBLE
    }
    

    //다른 fragment에서 동적 제어
    fun setTopBar(mytitle: String, isBackVisible: Boolean, isShow: Boolean){
        binding.mainTitleTv.text = mytitle
        binding.mainBackBtn.visibility = if (isBackVisible) View.VISIBLE else View.INVISIBLE
        if(isShow){
            binding.mainLockerBtn.visibility = View.VISIBLE
        }
        else{
            binding.mainLockerBtn.visibility = View.INVISIBLE
        }

    }
    fun setTopBar(isBackVisible: Boolean, isShow: Boolean){
        binding.mainTitleTv.text = title
        binding.mainBackBtn.visibility = if (isBackVisible) View.VISIBLE else View.INVISIBLE
        if(isShow){
            binding.mainLockerBtn.visibility = View.VISIBLE
        }
        else{
            binding.mainLockerBtn.visibility = View.INVISIBLE
        }
    }

    //알람 화면을 보여줄 때 세팅
    fun setTopBarNotice(){
        binding.mainTitleTv.text = "알림"
        binding.mainBackBtn.visibility = View.VISIBLE
        binding.mainBellBtn.visibility = View.VISIBLE
        binding.mainLockerBtn.visibility = View.INVISIBLE
        binding.mainBellBtn.isEnabled = false
    }
    fun resetTopBarNotice(){
        binding.mainBellBtn.isEnabled = true
    }

    fun changeTitle(newTitle: String){
        title = "${newTitle}의 화록"

    }

    //방문할 userId를 통해 친구 페이지로 이동
    fun navigateToFriendVisit(friendId: String) {
        binding.mainBnv.selectedItemId = R.id.friendFragment
        friendNavViewModel.openFriend(friendId)  // FriendListFragment가 observe
    }

    //보관함 버튼이 친구보관함으로 열리도록 설정
    fun setFriendLockerContext(friendId: String, friendName: String) {
        currentFriendId = friendId
        currentFriendName = friendName
    }

    //보관함 버튼 초기화 = 내 보관함으로 열게 함
    fun clearFriendLockerContext() {
        currentFriendId = null
        currentFriendName = null
    }

    //친구 아이디가 도착하기 전까지 보관함 버튼 잠금
    fun setLockerEnabled(enabled: Boolean) {
        binding.mainLockerBtn.isEnabled = enabled
        binding.mainLockerBtn.alpha = if (enabled) 1f else 0.5f // 비활성 시 시각적 피드백
    }


}