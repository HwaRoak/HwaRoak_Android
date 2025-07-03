package com.example.hwaroak.ui.diary

import android.R.attr.repeatCount
import android.R.attr.repeatMode
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ActivityDiaryFinishBinding


class DiaryFinishActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiaryFinishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiaryFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setImageAnimate()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //중간 이미지에 애니메이션 + 팝 애니메이션
    private fun setImageAnimate() {

        binding.diaryFinishResultImv.scaleX = 0f;
        binding.diaryFinishResultImv.scaleY = 0f;

        //setInterpolator로 애니메이션 진행 속도 조절 + OvershootInterpolator로 튕기는 듯한 (뿅)
        binding.diaryFinishResultImv.post {
            binding.diaryFinishResultImv.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(500)
                .withEndAction {  }
                .start()
        }

    }


}