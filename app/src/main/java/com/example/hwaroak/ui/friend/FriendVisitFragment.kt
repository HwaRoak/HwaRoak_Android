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
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient.friendService
import com.example.hwaroak.api.friend.access.FriendViewModel
import com.example.hwaroak.api.friend.access.FriendViewModelFactory
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.databinding.FragmentFriendVisitBinding
import com.example.hwaroak.ui.locker.LockerFragment
import com.example.hwaroak.ui.main.MainActivity
import kotlinx.coroutines.launch

class FriendVisitFragment : Fragment() {

    private var _binding: FragmentFriendVisitBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FriendViewModel

    private var originalStatusMessage: String? = null //status 저장용(방문하기 버튼 눌러도 status 출력)

    private var lastToast: Toast? = null

    //감정 게이지
    private val POSITIVE = setOf("차분한", "뿌듯한", "행복한", "기대됨", "설렘", "감사함", "신나는")
    private val NEGATIVE = setOf("슬픈", "화나는", "무료함", "피곤함", "짜증남", "외로움", "우울함", "스트레스")

    private var lockerBtn: ImageButton? = null //친구 보관함

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

        //초기 방어
        (activity as? MainActivity)?.setTopBar("불러오는 중...", isBackVisible = true, true)

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

            //UI topbar 갱신
            (activity as? MainActivity)?.setTopBar(
                "${data.nickname}의 화록",
                isBackVisible = true,
                true
            )

            //감정 게이지 적용
            val barType = toBarTypeFromEmotions(data.emotions)
            applyEmotionBarFriend(barType)

            //불씨 응답 observe
            viewModel.fireResponse.observe(viewLifecycleOwner) { data ->
                data?.let {
                    Log.d("불씨", "응답 메시지: ${it.message}, ${it.minutesLeft}분 남음")
                    //binding.friendVisitBubbleTv.text = it.message
                    lastToast?.cancel()
                    val appCtx = requireContext().applicationContext
                    lastToast = Toast.makeText(appCtx, it.message, Toast.LENGTH_SHORT)
                    lastToast?.show()

                    //한 번 표시했으면 바로 비워서 재진입 시 재발행 방지
                    viewModel.clearFireResponse()
                }
            }

            // 불키우기 버튼 클릭
            binding.friendFireupBtn.setOnClickListener {
                //클릭 시 바로 잠금
                binding.friendFireupBtn.isEnabled = false

                val token = getAccessTokenFromPreferences()
                val friendUserId = arguments?.getString("friendUserId")

                if (!token.isNullOrBlank() && !friendUserId.isNullOrBlank()) {
                    viewModel.sendFireToFriend("Bearer $token", friendUserId)
                }

                showFireAnimation()
                animateCharacterAndGaugeFire()

                //2.5초 뒤 다시 활성화
                viewLifecycleOwner.lifecycleScope.launch {
                    kotlinx.coroutines.delay(2500)
                    _binding?.friendFireupBtn?.isEnabled = true
                }
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

        //보관함 버튼이 보이는지 안 보이는지 모르겠음
        val lockerBtn = requireActivity().findViewById<ImageButton>(R.id.main_locker_btn)
        lockerBtn.visibility = View.VISIBLE

        //보관함 버튼 클릭 시 friendId 번들로 넘겨 lockerFragment로 전환
        lockerBtn.setOnClickListener {
            if (!isAdded) return@setOnClickListener  // 프래그먼트가 붙어있지 않으면 무시

            val friendId = viewModel.friendPage.value?.userId
                ?: arguments?.getString("friendUserId")
                ?: return@setOnClickListener

            val locker = LockerFragment().apply {
                arguments = android.os.Bundle().apply { putString("friendId", friendId) }
            }

            // parentFragmentManager 대신 Activity의 FragmentManager 사용
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_fragmentContainer, locker)
                .addToBackStack(null)
                .commit()
        }
    }

    //감정 게이지 bartype
    private fun toBarTypeFromEmotions(raw: String?): Int {
        if (raw.isNullOrBlank()) return 0
        val tokens = raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return 0

        var pos = 0
        var neg = 0
        for (t in tokens) {
            val term = t.replace(" ", "")
            when {
                POSITIVE.contains(term) -> pos++
                NEGATIVE.contains(term) -> neg++
            }
        }

        return when {
            neg == 0 && pos > 0 -> 1 // 전부 긍정
            pos == 0 && neg > 0 -> 5 // 전부 부정
            pos == neg           -> 2 // 반반
            neg > pos            -> 3 // 부정 많음
            else                 -> 4 // 긍정 많음
        }
    }

    private fun applyEmotionBarFriend(barType: Int) {
        val iv = binding.friendGuagebar // 게이지바 ImageView ID
        when (barType) {
            0 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_default)
            1 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_all_pos)
            2 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_half_half)
            3 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_neg_neg_pos)
            4 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_neg_pos_pos)
            5 -> iv.setImageResource(R.drawable.img_friend_emotion_gauge_all_neg)
        }
    }

    //불키우기 애니메이션
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
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(900)
            _binding?.friendVisitBubbleTv?.text = originalStatusMessage
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
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(900)
            _binding?.let { bindingNow ->
                listOf(bindingNow.friendFire1Imv, bindingNow.friendFire2Imv, bindingNow.friendFire3Imv)
                    .forEach { it.visibility = View.INVISIBLE }
            }
        }
    }

    private fun animateCharacterAndGaugeFire() {
        // 1. 캐릭터 눈 커짐 이미지 교체 (0.9초 뒤 원래 이미지로)
        binding.ivFriendCharacter.setImageResource(R.drawable.ic_friend_character_surprise)
        viewLifecycleOwner.lifecycleScope.launch {
            kotlinx.coroutines.delay(900)
            _binding?.ivFriendCharacter?.setImageResource(R.drawable.img_home_hwaroaki)
        }

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

    override fun onPause() {
        super.onPause()
        lastToast?.cancel()
        viewModel.clearFireResponse()
    }

    override fun onDestroyView() {
        lastToast?.cancel()
        viewModel.clearFireResponse()
        lastToast = null
        _binding = null
        super.onDestroyView()
    }
}
