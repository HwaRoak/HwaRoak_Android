package com.example.hwaroak.ui.friend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentFriendVisitBinding

class FriendVisitFragment : Fragment() {

    private var _binding: FragmentFriendVisitBinding? = null
    private val binding get() = _binding!!

    private var hasSentFireOnce = false // API 대체용: 처음 클릭 여부만 체크

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendVisitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.friendFireupBtn.setOnClickListener {
            // 말풍선 텍스트 변경
            //binding.tvSpeechBubble.text = "화록이 불타올라요!"

            // 불 애니메이션 실행
            showFireAnimation()

            // 캐릭터 & 게이지 불 커짐 효과
            animateCharacterAndGaugeFire()

            // API는 아직 연동 X
            if (!hasSentFireOnce) {
                hasSentFireOnce = true
                // sendFireApi()
            }
            //api 연동후 수정예정.. 서버에서 남은 시간 받아야 함
            Toast.makeText(requireContext(), "다음 알림은 59분 후에 전송돼요!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFireAnimation() {
        val fires = listOf(binding.friendFire1Imv, binding.friendFire2Imv, binding.friendFire3Imv)

        // 초기 상태
        fires.forEach {
            it.visibility = View.VISIBLE
            it.scaleX = 0f
            it.scaleY = 0f
        }

        //api 연동후 수정..
        binding.friendVisitBubbleTv.text = "뽀동이님의 화록이 불타올라요!"
        binding.friendVisitBubbleTv.postDelayed({
            binding.friendVisitBubbleTv.text = "뽀둥이님은 오늘 즐거워요"
        }, 900L)

        // 각 불 애니메이션
        val duration = 300L
        val delay = 150L


        fires[0].post {
            fires[0].animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay(0)
                .start()
        }

        fires[1].post {
            fires[1].animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay(duration / 2 + delay)
                .start()
        }

        fires[2].post {
            fires[2].animate()
                .scaleX(1f).scaleY(1f)
                .setInterpolator(OvershootInterpolator(2f))
                .setDuration(duration)
                .setStartDelay(duration + delay)
                .start()
        }
        // 일정 시간 뒤에 모두 사라지게 처리 (예: 2.5초 뒤)
        fires[0].postDelayed({
            fires.forEach { it.visibility = View.INVISIBLE }
        }, 900L)
    }

    private fun animateCharacterAndGaugeFire() {
        // 1. 캐릭터 눈 커짐 이미지 교체 (0.9초 뒤 원래 이미지로)
        binding.ivFriendCharacter.setImageResource(R.drawable.ic_friend_character_surprise)
        binding.ivFriendCharacter.postDelayed({
            binding.ivFriendCharacter.setImageResource(R.drawable.img_home_hwaroaki)
        }, 900)

        val fireRec = binding.friendFireRecIn

        // 1. 먼저 확대 (확대한 상태 유지)
        fireRec.animate()
            .scaleX(1.3f).scaleY(1.3f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                // 2. 확대된 상태에서 바로 흔들림 시작
                startFireShakeAnimation(fireRec) {
                    // 3. 흔들림 종료 후 원래 크기로 복귀
                    fireRec.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(300)
                        .start()
                }
            }
            .start()
    }

    private fun startFireShakeAnimation(view: View, onEnd: () -> Unit) {
        val animator = ObjectAnimator.ofFloat(
            view,
            "rotation",
            -12f, 12f, -10f, 10f, -6f, 6f, 0f
        )
        animator.duration = 1400L  // 느리게
        animator.interpolator = OvershootInterpolator()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onEnd()
            }
        })
        animator.start()
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
