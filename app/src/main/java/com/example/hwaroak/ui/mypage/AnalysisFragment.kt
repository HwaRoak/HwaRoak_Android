package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.R
import com.example.hwaroak.data.MonthViewModel
import com.example.hwaroak.databinding.FragmentAnalysisBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val monthViewModel: MonthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        setupClickListeners()
        observeMonthChanges()

        // 원형 그래프 함수 호출
        initPieChart()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnPreviousAnalysis.setOnClickListener {
            monthViewModel.previousMonth()
        }

        binding.btnNextAnalysis.setOnClickListener {
            monthViewModel.nextMonth()
        }
    }

    private fun observeMonthChanges() {
        lifecycleScope.launch {
            monthViewModel.monthData.collectLatest { state ->
                // 텍스트 업데이트
                binding.btnPreviousAnalysis.text = state.previousMonthText
                binding.monthlyNumberDiaryTv.text = state.currentMonthText
                binding.btnNextAnalysis.text = state.nextMonthText

                // '이전 달' 버튼 상태 업데이트
                updateButtonState(binding.btnPreviousAnalysis, state.isPreviousEnabled)

                // '다음 달' 버튼 상태 업데이트
                updateButtonState(binding.btnNextAnalysis, state.isNextEnabled)
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

    // 원형 그래프 함수
    private fun initPieChart() {
        // 그래프 데이터 비율
        val emotionRatio = listOf(
            PieEntry(25f),
            PieEntry(75f),
            PieEntry(0f),
            PieEntry(0f)
        )

        // 그래프 데이터별 색상 지정
        val pieColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.comfy),
            ContextCompat.getColor(requireContext(), R.color.happy),
            ContextCompat.getColor(requireContext(), R.color.depressed),
            ContextCompat.getColor(requireContext(), R.color.angry)
        )

        // 데이터별 스타일링을 위해 DataSet 생성(label은 필요 없어서 공백으로 둠)
        val dataSet = PieDataSet(emotionRatio, "")

        // slice의 색상을 설정해준다.
        dataSet.colors = pieColors

        // 지금 서비스에서는 그래프에 퍼센트로 표시하지 않으므로 false
        dataSet.setDrawValues(false)

        binding.emotionPiechart.apply {
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
}