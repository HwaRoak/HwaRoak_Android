package com.example.hwaroak.ui.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.LockerItemRVAdaptor
import com.example.hwaroak.data.LockerItem
import com.example.hwaroak.databinding.FragmentLockerBinding
import com.example.hwaroak.ui.main.MainActivity

class LockerFragment : Fragment() {

    private var _binding: FragmentLockerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // XML 레이아웃을 화면에 붙이기
        _binding = FragmentLockerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 불러오기
        val LockerRecyclerView: RecyclerView = binding.lockerItemRv

        //샘플 데이터(추후 서버나 데이터베이스 아이템 목록을 가져오기)
        val items = mutableListOf<LockerItem?>()
        // 획득한 아이템 1개 추가
        items.add(LockerItem(1, "불타는 쿠키", R.drawable.img_item_chicken)) // R.drawable.img_flame_cookie는 실제 이미지 리소스

        // 나머지 14칸을 빈칸(null)으로 채우기
        for (i in 0 until 14) {
            items.add(null)
        }

        // 어댑터를 생성하고 RecyclerView에 연결
        val adapter = LockerItemRVAdaptor(items)
        LockerRecyclerView.adapter = adapter

        binding.lockerCloseBtn.setOnClickListener {
            // 1. 현재 프래그먼트를 호스팅하는 액티비티를 가져오고
            // 2. 해당 액티비티가 MainActivity인지 확인하고, 맞다면 selectTab 함수(mainactivity에 구현되어 있음)를 호출
            (activity as? MainActivity)?.selectTab(R.id.homeFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}