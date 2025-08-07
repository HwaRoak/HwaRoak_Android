package com.example.hwaroak.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.AgreeViewPagerAdatpor
import com.example.hwaroak.data.AgreeViewModel
import com.example.hwaroak.databinding.ActivityAgreeBinding

class AgreeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreeBinding
    private lateinit var viewPager: ViewPager2

    private val vm: AgreeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewPager 설정
        viewPager = binding.agreeViewpager
        viewPager.adapter = AgreeViewPagerAdatpor(this)
        viewPager.isUserInputEnabled = false

        binding.agreeBackBtn.setOnClickListener {
            val intent = Intent(this, LoginKakaoActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewPager.currentItem == 1 || viewPager.currentItem == 2) {
                    // 두 번째 페이지(1)라면 첫 페이지(0)로
                    viewPager.currentItem = 0
                } else {
                    // 이미 첫 페이지(0)이면 원래 뒤로가기
                    finish()
                }
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //viewpager 이동을 위한 함수
    fun goToDetailPage() {
        viewPager.currentItem = 1
    }
    fun goToMainPage() {
        viewPager.currentItem = 0
    }
    fun goToDetailPage2(){
        viewPager.currentItem = 2
    }
}