package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hwaroak.R
import com.example.hwaroak.data.MonthViewModel
import com.example.hwaroak.databinding.FragmentAnalysisBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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
}