package com.example.hwaroak.ui.friend

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentFriendBinding

class FriendFragment : Fragment() {

    // 뷰바인딩 선언
    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    var isManageMode = false //현재 관리 모드 여부
    lateinit var adapter: FriendAdapter // recyclerView 어뎁터
    lateinit var friendList: MutableList<FriendData>  //친구 목록 리스트

    // 뷰바인딩 초기화 및 레이아웃
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    // 뷰 생성 후 로직 처리
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 관리 텍스트뷰 밑줄 처리
        binding.friendManage.paintFlags =
            binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // 친구 목록 더미 데이터 생성
        friendList = MutableList(10) {
            FriendData("포둥이", "현재 슬퍼요ㅠㅠ")
        }

        // 친구 추가 버튼용 아이템 추가
        friendList.add(FriendData("", "", isAddButton = true))

        adapter = FriendAdapter(friendList) {
            // 친구 추가 버튼 클릭 시 AddFriendFragment로 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AddFriendFragment())
                .addToBackStack(null)
                .commit()
        }

        // recyclerview 세팅
        binding.friendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerview.adapter = adapter

        //한번에 삭제하기 밑줄 처리
        binding.friendManage.paintFlags =
            binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        //관리 버튼 클릭 이벤트 설정
        binding.friendManage.setOnClickListener {
            isManageMode = !isManageMode //관리 모드

            // 기존 버튼 제거
            friendList.removeAll { it.isAddButton || it.isDeleteAllButton }

            if (isManageMode) {
                // 관리 모드 진입: 각 친구에 삭제 아이콘 표ㅛ시
                friendList.forEach { it.isDeletable = true }

                // 전체 삭제 버튼 추가
                friendList.add(FriendData("", "", isDeleteAllButton = true))
                binding.friendManage.text = "완료" //관리 텍스트뷰를 누르면 완료로 변경
            } else {
                // 일반 모드 복귀: 삭제 아이콘 제거
                friendList.forEach { it.isDeletable = false }

                // 친구 추가 버튼 추가
                friendList.add(FriendData("", "", isAddButton = true))
                binding.friendManage.text = "관리" //완료 텍스트뷰를 관리 텍스트뷰로 변경
            }

            //밑줄 유지
            binding.friendManage.paintFlags =
                binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}