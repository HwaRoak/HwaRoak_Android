package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentAnnouncementBinding
import com.example.hwaroak.databinding.FragmentMypageBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // 원형 그래프 함수 호출
        initPieChart()

        binding.btnMyinfo.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnCheckDetail.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AnalysisFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.announcement.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AnnouncementFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
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

        // 데이터별
        val dataSet = PieDataSet(emotionRatio, "")

        dataSet.colors = pieColors

        dataSet.setDrawValues(false)

        binding.emotionPiechart.apply {
            data = PieData(dataSet)

            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = true
            holeRadius = 60f
            setTouchEnabled(false)
            animateY(1200, Easing.EaseInOutCubic)

            animate()
        }
    }
}