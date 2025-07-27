package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.adaptor.AnnouncementRVAdaptor
import com.example.hwaroak.data.AnnouncementData
import com.example.hwaroak.databinding.FragmentAnnouncementBinding

class AnnouncementFragment : Fragment() {

    private var _binding: FragmentAnnouncementBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val announcementItems = listOf(
            AnnouncementData("Ver. 1.0", "공지사항 내용입니다."),
            AnnouncementData("Ver. 2.0", "새로운 기능이 추가되었습니다.", true)
        )

        binding.announcementRv.layoutManager = LinearLayoutManager(requireContext())
        binding.announcementRv.adapter = AnnouncementRVAdaptor(announcementItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}