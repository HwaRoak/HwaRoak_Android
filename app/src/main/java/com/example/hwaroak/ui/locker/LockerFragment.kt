package com.example.hwaroak.ui.locker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.adaptor.LockerItemRVAdaptor
import com.example.hwaroak.data.ItemViewModel
import com.example.hwaroak.data.LockerItem
import com.example.hwaroak.data.NoticeItem
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
    //아이템 변경을 위한 viewmodel선언
    private lateinit var itemViewModel: ItemViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //아이템 더미데이터
        val dummyItems = listOf(
            LockerItem(1, "tissue", R.drawable.img_item_tissue),
            LockerItem(2, "cup", R.drawable.img_item_cup),
            LockerItem(3, "paper", R.drawable.img_item_paper),
            LockerItem(4, "egg",R.drawable.img_item_egg),
            LockerItem(5, "mashmellow",R.drawable.img_item_mashmellow),
            LockerItem(6, "meat",R.drawable.img_item_meat),
            LockerItem(7, "coal",R.drawable.img_item_coal),
            LockerItem(8, "tire",R.drawable.img_item_tire),
            LockerItem(9, "trash",R.drawable.img_item_trash),
            LockerItem(10, "chicken",R.drawable.img_item_chicken),
            LockerItem(11, "soup",R.drawable.img_item_soup),
            LockerItem(12, "ruby",R.drawable.img_item_ruby),
            LockerItem(13, "chopstick",R.drawable.img_item_chopstick),
            LockerItem(14, "potato",R.drawable.img_item_potato),
            LockerItem(15, "cheeze",R.drawable.img_item_cheeze)
        )
        //viewmodel
        itemViewModel = ViewModelProvider(requireActivity())[ItemViewModel::class.java]

        // RecyclerView 불러오기
        val LockerRecyclerView: RecyclerView = binding.lockerItemRv

        //샘플 데이터(추후 서버나 데이터베이스 아이템 목록을 가져오기)
        val items = mutableListOf<LockerItem?>()
        // 획득한 아이템 1개 추가
        items.addAll(dummyItems) //dummyitems안의 모든 아이템 추가

        // 특정 총 개수(예: 20개)를 유지하고, 나머지 null로
        val totalLockerSlots = 20
        val itemsToAddNull = totalLockerSlots - items.size // 20 - 15 = 5개의 null 추가

        for (i in 0 until itemsToAddNull) {
            items.add(null) // 빈칸(null)으로 채우기
        } // 이 시점에서 'items' 리스트는 15개의 dummyItems + 5개의 null = 총 20개의 요소

        // 어댑터를 생성하고 RecyclerView에 연결
        val adapter = LockerItemRVAdaptor(items) { selectedItem ->
            if (selectedItem != null) {
//                itemViewModel.setHomeItem(selectedItem)
//                (activity as? MainActivity)?.selectTab(R.id.homeFragment)
                showConfirmDialog(selectedItem)
            }
        }

        LockerRecyclerView.adapter = adapter

        binding.lockerCloseBtn.setOnClickListener {
            // 1. 현재 프래그먼트를 호스팅하는 액티비티를 가져오고
            // 2. 해당 액티비티가 MainActivity인지 확인하고, 맞다면 selectTab 함수(mainactivity에 구현되어 있음)를 호출
            (activity as? MainActivity)?.selectTab(R.id.homeFragment)
        }
    }

    // 아이템 변경 확인 다이얼로그를 띄우는 함수 추가
    private fun showConfirmDialog(item: LockerItem) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_item_confirm) // 다이얼로그 레이아웃 설정

        val btnCancel: Button = dialog.findViewById(R.id.item_confirm_cancel_btn)
        val btnChange: Button = dialog.findViewById(R.id.item_confirm_change_btn)

        // '취소' 버튼 클릭 리스너
        btnCancel.setOnClickListener {
            dialog.dismiss() // 다이얼로그 닫기
        }

        // '변경' 버튼 클릭 리스너
        btnChange.setOnClickListener {
            // '변경'이 클릭되었을 때만 실제 아이템 변경 로직 실행
            itemViewModel.setHomeItem(item)
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그의 기본 배경을 투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 표시
        dialog.show()
    }
    

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}