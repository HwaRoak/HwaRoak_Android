package com.example.hwaroak.ui.friend

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

            // API는 아직 연동 X, 단 첫 클릭 시 호출했었다고 표시
            if (!hasSentFireOnce) {
                hasSentFireOnce = true
                // sendFireApi()
            }
            //처음 알림에는 안 나오게 할지 수정 예정..
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

        // 2. 게이지 옆 불: 기울이고 커졌다가 복귀 (전체 0.9초)
        val fireRec = binding.friendFireRecIn
        fireRec.animate()
            .rotation(15f)
            .scaleX(1.4f).scaleY(1.4f)
            .setDuration(450)
            .withEndAction {
                fireRec.animate()
                    .rotation(0f)
                    .scaleX(1f).scaleY(1f)
                    .setDuration(450)
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
