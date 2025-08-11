package com.example.hwaroak.ui.mypage

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.R
import com.example.hwaroak.api.mypage.access.MemberViewModel
import com.example.hwaroak.data.AnalysisData
import com.example.hwaroak.data.EmotionSummary
import com.example.hwaroak.data.MonthViewModel
import com.example.hwaroak.databinding.FragmentAnalysisBinding
import com.example.hwaroak.ui.main.MainActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.getValue

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val monthViewModel: MonthViewModel by viewModels()
    private val memberViewModel: MemberViewModel by activityViewModels()

    private lateinit var pref: SharedPreferences
    private lateinit var accessToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var pref2: SharedPreferences = requireActivity().getSharedPreferences("user", MODE_PRIVATE)
        val name = pref2.getString("nickname", "") ?: ""
        val nickname = pref2.getString("cachedNickname", "") ?: ""

        var title = if(nickname == "") "${name}의 화록" else "${nickname}의 화록"

        (activity as? MainActivity)?.setTopBar(title, isBackVisible = true, false)

        setupClickListeners()
        observeMonthChanges()

    }

    private fun setupClickListeners() {
        binding.analysisPreviousBtn.setOnClickListener {
            monthViewModel.previousMonth()
        }

        binding.analysisNextBtn.setOnClickListener {
            monthViewModel.nextMonth()
        }
    }

    private fun observeMonthChanges() {
        lifecycleScope.launch {
            monthViewModel.monthData.collectLatest { state ->
                // 텍스트 업데이트
                binding.analysisPreviousBtn.text = state.previousMonthText
                binding.analysisNumberDiaryTv.text = state.currentMonthText
                binding.analysisNextBtn.text = state.nextMonthText

                // '이전 달' 버튼 상태 업데이트
                updateButtonState(binding.analysisPreviousBtn, state.isPreviousEnabled)

                // '다음 달' 버튼 상태 업데이트
                updateButtonState(binding.analysisNextBtn, state.isNextEnabled)
            }
        }
    }

    // 버튼 상태를 업데이트하는 공통 함수
    private fun updateButtonState(button: MaterialButton, isEnabled: Boolean) {
        button.isEnabled = isEnabled
        if (isEnabled) {
            // 활성화 상태일 때 원래의 커스텀 배경으로 되돌리기
            button.setBackgroundResource(R.drawable.bg_diary_write_btn)
        } else {
            // 비활성화 상태일 때의 커스텀 배경 설정
            button.setBackgroundResource(R.drawable.bg_diary_write_no_btn)
        }
    }

    private fun updateUi(data: AnalysisData) {
        binding.analysisNumberOfDiaryTv.text = "${data.diaryCount}개"
        binding.analysisSummaryTv.text = data.message

        data.emotionSummary?.let { summary ->
            binding.analysisComfyPercentTv.text = "${summary.CALM.percent.toInt()}%"
            binding.analysisHappyPercentTv.text = "${summary.HAPPY.percent.toInt()}%"
            binding.analysisDepressedPercentTv.text = "${summary.SAD.percent.toInt()}%"
            binding.analysisAngryPercentTv.text = "${summary.ANGRY.percent.toInt()}%"
        } ?: run {
            binding.analysisComfyPercentTv.text = "0%"
            binding.analysisHappyPercentTv.text = "0%"
            binding.analysisDepressedPercentTv.text = "0%"
            binding.analysisAngryPercentTv.text = "0%"
        }

        initPiechart(data.emotionSummary)
    }

    // 원형 그래프 함수
    private fun initPiechart(summary: EmotionSummary?) {
        val emotionRatio: List<PieEntry>
        val pieColors: List<Int>

        val totalPercent = summary?.let {
            it.CALM.percent + it.HAPPY.percent + it.SAD.percent + it.ANGRY.percent
        } ?: 0.0

        if (summary != null && totalPercent != 0.0) {
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
        binding.analysisEmotionPiechart.apply {
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

            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }

}