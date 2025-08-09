package com.example.hwaroak.ui.friend

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient.friendService
import com.example.hwaroak.api.friend.access.FriendViewModel
import com.example.hwaroak.api.friend.access.FriendViewModelFactory
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.databinding.FragmentFriendVisitBinding

class FriendVisitFragment : Fragment() {

    private var _binding: FragmentFriendVisitBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FriendViewModel
    private var hasSentFireOnce = false // API 대체용: 처음 클릭 여부만 체크

    private var originalStatusMessage: String? = null //status 저장용(방문하기 버튼 눌러도 status 출력)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendVisitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. viewModel 초기화
        viewModel = ViewModelProvider(
            this,
            FriendViewModelFactory(FriendRepository(friendService))
        )[FriendViewModel::class.java]

        //UI 초기화 받아오는데 오래 걸릴 경우 표시
        binding.tvFriendTitle.text = "불러오는 중..."
        binding.friendVisitBubbleTv.text = "불러오는 중..."

        // 2. LiveData observe
        Log.d("FriendVisitFragment", "observe 진입 전")
        viewModel.friendPage.observe(viewLifecycleOwner) { data ->
            Log.d("FriendVisitFragment", "닉네임: ${data.nickname}, 메시지: ${data.message}")

            // 저장해놓기
            originalStatusMessage = data.message

            // UI 갱신
            binding.tvFriendTitle.text = "${data.nickname}의 화록"
            binding.friendVisitBubbleTv.text = data.message

            //불씨 응답 observe
            viewModel.fireResponse.observe(viewLifecycleOwner) { data ->
                data?.let {
                    Log.d("불씨", "응답 메시지: ${it.message}, ${it.minutesLeft}분 남음")
                    //binding.friendVisitBubbleTv.text = it.message
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }

            // 방문하기 버튼 클릭
            binding.friendFireupBtn.setOnClickListener {
                val token = getAccessTokenFromPreferences()
                val friendUserId = arguments?.getString("friendUserId")

                if (!token.isNullOrBlank() && !friendUserId.isNullOrBlank()) {
                    viewModel.sendFireToFriend("Bearer $token", friendUserId)
                }

                showFireAnimation()
                animateCharacterAndGaugeFire()
            }

        }

        // 3. 토큰 설정 (Bearer 중복 제거)
        val token = getAccessTokenFromPreferences()?.replace("Bearer ", "")
        val friendUserId = arguments?.getString("friendUserId")

        if (!token.isNullOrBlank() && !friendUserId.isNullOrBlank()) {
            viewModel.visitFriendPage("Bearer $token", friendUserId)
        } else {
            Log.e("FriendVisitFragment", "토큰 또는 유저 ID가 null입니다.")
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

        val nickname = binding.tvFriendTitle.text.toString().replace("의 화록", "")
        binding.friendVisitBubbleTv.text = "${nickname}의 화록이 불타올라요!"

        //900ms 후 원래 메시지 복구
        binding.friendVisitBubbleTv.postDelayed({
            originalStatusMessage?.let {
                binding.friendVisitBubbleTv.text = it
            }
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

    private fun getAccessTokenFromPreferences(): String? {
        val pref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        return pref.getString("accessToken", null)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
