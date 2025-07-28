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
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.home.repository.ItemRepository
import com.example.hwaroak.data.ItemViewModel
import com.example.hwaroak.data.ItemViewModelFactory
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

        // ItemService 인스턴스를 HwaRoakClient에서 가져옵니다. (NetworkModule 대신 HwaRoakClient 사용)
        val itemService = HwaRoakClient.itemApiService // HwaRoakClient.kt에 정의된 itemApiService 사용

        // ItemRepository 인스턴스를 ItemService와 함께 생성합니다.
        val itemRepository = ItemRepository(itemService)

        // ViewModel 초기화: ItemViewModelFactory를 사용하여 ItemRepository를 주입합니다.
        itemViewModel = ViewModelProvider(requireActivity(), ItemViewModelFactory(itemRepository))[ItemViewModel::class.java]

        // RecyclerView 불러오기
        val lockerRecyclerView: RecyclerView = binding.lockerItemRv

        val adapter = LockerItemRVAdaptor(emptyList()) { selectedItem ->
            if (selectedItem != null) {
                showConfirmDialog(selectedItem)
            }
        }
        lockerRecyclerView.adapter = adapter

        // 보유 아이템 리스트 불러오기
        itemViewModel.loadUserItems() // <- 이 함수가 ViewModel에 구현돼 있어야 함

        // LiveData observe
        itemViewModel.rewardItemList.observe(viewLifecycleOwner) { items ->
            val filledItems = items.toMutableList()
            repeat(20 - filledItems.size) { filledItems.add(null) } // 빈칸 추가
            adapter.updateData(filledItems)
        }

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