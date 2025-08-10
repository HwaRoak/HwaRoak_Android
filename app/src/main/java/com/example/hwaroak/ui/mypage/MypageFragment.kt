package com.example.hwaroak.ui.mypage

import android.R.attr.data
import android.R.attr.description
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.activityViewModels
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.mypage.access.MemberViewModel
import com.example.hwaroak.api.mypage.access.MemberViewModelFactory
import com.example.hwaroak.api.mypage.model.MypageInfoResponse
import com.example.hwaroak.api.mypage.model.EmotionSummary as ApiEmotionSummary
import com.example.hwaroak.api.mypage.model.EmotionData as ApiEmotionData
import com.example.hwaroak.api.mypage.repository.MemberRepository
import com.example.hwaroak.data.EmotionSummary
import com.example.hwaroak.data.MypageData
import com.example.hwaroak.data.EmotionData
import com.example.hwaroak.databinding.DialogLogoutCheckBinding
import com.example.hwaroak.databinding.FragmentMypageBinding
import com.example.hwaroak.ui.friend.AddFriendFragment
import com.example.hwaroak.ui.login.LoginKakaoActivity
import com.example.hwaroak.ui.main.MainActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kakao.sdk.auth.TokenManagerProvider
import com.kakao.sdk.user.UserApiClient

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val memberViewModel: MemberViewModel by activityViewModels {
        MemberViewModelFactory(MemberRepository(HwaRoakClient.memberService))
    }
    //엑세스 토큰
    //유저 정보를 담을 sharedPreference
    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**상단바 설정**/
        (activity as? MainActivity)?.setTopBar(isBackVisible = true)

        setupNavigation()
        setupLogout()

        pref = requireContext().getSharedPreferences("user", MODE_PRIVATE)
        accessToken = pref.getString("accessToken", "").toString()

        // 캐시된 닉네임 먼저 표시
        val cachedNickname = pref.getString("cachedNickname", null)
        binding.mypageNicknameTv.text = cachedNickname ?: "닉네임 불러오는 중..."

        // 1. 최초에 한 번 사용자 정보를 요청 (또는 화면에 보일 때마다)
        memberViewModel.getMemberInfo(accessToken)

        // 2. ViewModel의 사용자 정보를 관찰
        memberViewModel.memberInfoResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                binding.mypageNicknameTv.text = data.nickname
                // 닉네임을 SharedPreferences에 저장
                pref.edit().putString("cachedNickname", data.nickname).apply()
            }
            result.onFailure {
                Log.d("member", "불러오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "회원 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }

        memberViewModel.getMypageInfo(accessToken)

        memberViewModel.mypageInfoResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { data ->
                updateUi(data.toMypageData())
            }
        }
    }

    private fun updateUi(data: MypageData) {
        // 공통적으로 표시되는 데이터(일기 개수, 다음 아이템 관련 정보)
        binding.mypageCountDiaryTv.text = "${data.totalDiary}개"
        binding.mypageItemImageIv.setImageResource(
            when (data.itemId) {
                "cup" -> R.drawable.img_item_cup
                // …다른 아이템 매핑
                "cheeze" -> R.drawable.img_item_cheeze
                "chicken" -> R.drawable.img_item_chicken
                "chopstick" -> R.drawable.img_item_chopstick
                "coal" -> R.drawable.img_item_coal
                "egg" -> R.drawable.img_item_egg
                "mashmellow" -> R.drawable.img_item_mashmellow
                "meat" -> R.drawable.img_item_meat
                "paper" -> R.drawable.img_item_paper
                "potato" -> R.drawable.img_item_potato
                "ruby" -> R.drawable.img_item_ruby
                "soup" -> R.drawable.img_item_soup
                "tire" -> R.drawable.img_item_tire
                "tissue" -> R.drawable.img_item_tissue
                "trash" -> R.drawable.img_item_trash
                else -> R.drawable.img_item_tissue
            }
        )
        binding.mypageDdayTv.text = "D-${data.reward}"

        // 감정분석 관련 데이터
        data.emotionSummary?.let { summary ->
            binding.mypageComfyPercentTv.text = "${summary.CALM.percent.toInt()}%"
            binding.mypageHappyPercentTv.text = "${summary.HAPPY.percent.toInt()}%"
            binding.mypageDepressedPercentTv.text = "${summary.SAD.percent.toInt()}%"
            binding.mypageAngryPercentTv.text = "${summary.ANGRY.percent.toInt()}%"
        } ?: run {
            binding.mypageComfyPercentTv.text = "0%"
            binding.mypageHappyPercentTv.text = "0%"
            binding.mypageDepressedPercentTv.text = "0%"
            binding.mypageAngryPercentTv.text = "0%"
        }

        // 원형 그래프 함수 호출
        initPiechart(data.emotionSummary)
    }

    private fun initPiechart(summary: EmotionSummary?) {
        val emotionRatio: List<PieEntry>
        val pieColors: List<Int>

        if (summary != null) {
            // 그래프 데이터 비율
            emotionRatio = listOf(
                PieEntry(summary.CALM.percent.toFloat()),
                PieEntry(summary.HAPPY.percent.toFloat()),
                PieEntry(summary.SAD.percent.toFloat()),
                PieEntry(summary.ANGRY.percent.toFloat())
            )
            // 그래프 데이터별 색상 지정
            pieColors = listOf(
                ContextCompat.getColor(requireContext(), R.color.comfy),
                ContextCompat.getColor(requireContext(), R.color.happy),
                ContextCompat.getColor(requireContext(), R.color.depressed),
                ContextCompat.getColor(requireContext(), R.color.angry)
            )
        } else {
            emotionRatio = listOf(PieEntry(100f))
            pieColors = listOf(ContextCompat.getColor(requireContext(), R.color.none))
        }

        // 데이터별 스타일링을 위해 DataSet 생성(label은 필요 없어서 공백으로 둠)
        val dataSet = PieDataSet(emotionRatio, "").apply {
            colors = pieColors // slice의 색상을 설정해준다.
            setDrawValues(false) // 지금 서비스에서는 그래프에 퍼센트로 표시하지 않으므로 false
        }

        // 세부 스타일링 관련
        binding.mypageEmotionPiechart.apply {
            data = PieData(dataSet)

            // description.isEnabled : 차트 설명 유무 설정
            // legend.isEnabled : 범례 유무 설정
            // isRotationEnabled : 차트 회전 활성화 여부 설정
            // holeRadius : 차트 중간 구멍 크기 설정
            // setTouchEnabled : slice 터치 활성화 여부 설정
            // animateY(1200, Easing.EaseInOutCubic) : 애니메이션 시간, 효과 설정
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 55f
            setTouchEnabled(false)
            // animateY(1200, Easing.EaseInOutCubic)

            animate()
        }
    }

    private fun MypageInfoResponse.toMypageData(): MypageData {
        val uiEmotionSummary = this.emotionSummary?.let { apiSummary: ApiEmotionSummary ->
            EmotionSummary(
                CALM = EmotionData(percent = apiSummary.CALM.percent),
                HAPPY = EmotionData(percent = apiSummary.HAPPY.percent),
                SAD = EmotionData(percent = apiSummary.SAD.percent),
                ANGRY = EmotionData(percent = apiSummary.ANGRY.percent)
            )
        }
        return MypageData(
            emotionSummary = uiEmotionSummary,
            totalDiary = this.totalDiary,
            itemId = this.nextItemName, // `nextItemName`을 `itemId`로 매핑
            reward = this.reward
        )
    }
    private fun setupNavigation() {
        binding.mypageMyinfoBtn.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }

        binding.mypageDetailBtn.setOnClickListener {
            replaceFragment(AnalysisFragment())
        }

        binding.mypageAnnouncementTv.setOnClickListener {
            replaceFragment(AnnouncementFragment())
        }

        binding.mypageNotificationSettingTv.setOnClickListener {
            replaceFragment(SettingFragment())
        }

        binding.mypageTermsTv.setOnClickListener {
            replaceFragment(TermsFragment())
        }

        binding.mypageSearchUserTv.setOnClickListener {
            replaceFragment(AddFriendFragment())
        }
    }

    private fun setupLogout() {
        binding.mypageLogoutTv.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showLogoutDialog() {
        // 1. 다이얼로그 레이아웃으로 뷰 바인딩 객체 생성
        val dialogBinding = DialogLogoutCheckBinding.inflate(LayoutInflater.from(requireContext()))

        // 2. AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setView(dialogBinding.root) // 뷰 바인딩의 root 뷰를 설정
            .create()

        // 3. 취소 버튼 리스너와 로그아웃 버튼 리스너
        dialogBinding.dialogCancelBtn.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        dialogBinding.dialogLogoutBtn.setOnClickListener {
            // 여기에 로직 짜주시면 될 것 같습니다!!
            kakaoLogout()
            // 로직 처리 후 다이얼로그 닫기
            dialog.dismiss()
        }

        // 4. 다이얼로그 표시
        dialog.show()
    }

    private fun kakaoLogout() {
        // 1) 카카오 서버에 로그아웃 요청
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("KakaoLogout", "로그아웃 실패", error)
            } else {
                Log.i("KakaoLogout", "로그아웃 성공")
            }
            // 2) 로컬에 저장된 토큰(액세스/리프레시) 완전 삭제
            TokenManagerProvider
                .instance
                .manager
                .clear()
        }

        val intent = Intent(requireActivity(), LoginKakaoActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}