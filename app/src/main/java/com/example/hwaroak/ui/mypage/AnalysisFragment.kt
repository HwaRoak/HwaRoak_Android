package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentAnalysisBinding
import com.example.hwaroak.databinding.FragmentAnnouncementBinding

class AnalysisFragment : Fragment() {

    lateinit var binding: FragmentAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        return binding.root
    }
}