package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat.animate
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentMypageBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class MypageFragment : Fragment() {

    lateinit var binding: FragmentMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        initPieChart()

        return binding.root
    }

    private fun initPieChart() {
        val emotionRatio = listOf(
            PieEntry(25f),
            PieEntry(75f),
            PieEntry(0f),
            PieEntry(0f)
        )

        val pieColors = listOf(
            resources.getColor(R.color.comfy, null),
            resources.getColor(R.color.happy, null),
            resources.getColor(R.color.depressed, null),
            resources.getColor(R.color.angry, null)
        )
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