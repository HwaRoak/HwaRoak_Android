package com.example.hwaroak.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.NoticeItemRVAdaptor
import com.example.hwaroak.data.NoticeItem
import com.example.hwaroak.databinding.FragmentNoticeBinding
import com.example.hwaroak.ui.main.MainActivity

class NoticeFragment : Fragment() {

    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //알림 더미데이터
        val dummyNotices = listOf(
            NoticeItem("제목 1", "내용 1111111."),
            NoticeItem("제목 2", "내용 2222222."),
            NoticeItem("제목 3", "내용 3333333."),
            NoticeItem("제목 4", "내용 4444444."),
            NoticeItem("제목 5", "내용 5555555."),
            NoticeItem("제목 6", "내용 6666666."),
            NoticeItem("제목 7", "내용 7777777."),
            NoticeItem("제목 8", "내용 8888888."),
            NoticeItem("제목 9", "내용 9999999."),
            NoticeItem("제목 10", "내용 10101010.")
        )
        //알림 recyclerveiw 어댑터 선언, 설정, inflate
        val adapter = NoticeItemRVAdaptor(dummyNotices)
        binding.rvNoticeContainer.adapter = adapter
        binding.rvNoticeContainer.layoutManager = LinearLayoutManager(requireContext())

        binding.noticeBackBtn.setOnClickListener {
            // 뒤로가기 버튼 클릭 시 MainActivity의 상단 바를 다시 보이게 함
            (activity as? MainActivity)?.showMainTopBar()
            // HomeFragment로 이동하거나 백 스택에서 현재 프래그먼트를 제거
            (activity as? MainActivity)?.selectTab(R.id.homeFragment)
        }
        // 추후 필요시 여기에 다른 로직 구현
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 프래그먼트 뷰가 파괴될 때 MainActivity의 상단 바를 다시 보이게 함(프래그먼트가 완전히 제거될 때 호출)
        (activity as? MainActivity)?.showMainTopBar()
        _binding = null
    }
}