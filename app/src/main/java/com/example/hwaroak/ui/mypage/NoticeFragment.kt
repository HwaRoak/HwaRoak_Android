package com.example.hwaroak.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.NoticeRVAdaptor
import com.example.hwaroak.data.Notice
import com.example.hwaroak.databinding.FragmentMypageBinding
import com.example.hwaroak.databinding.FragmentNoticeBinding

class NoticeFragment : Fragment() {

    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noticeItems = listOf(
            Notice("Ver. 1.0", "공지사항 내용입니다."),
            Notice("Ver. 2.0", "새로운 기능이 추가되었습니다.", true)
        )

        binding.noticeRv.layoutManager = LinearLayoutManager(requireContext())
        binding.noticeRv.adapter = NoticeRVAdaptor(noticeItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}