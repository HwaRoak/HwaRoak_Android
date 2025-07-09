package com.example.hwaroak.ui.friend

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.databinding.FragmentFriendBinding

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    var isManageMode = false
    lateinit var adapter: FriendAdapter
    lateinit var friendList: MutableList<FriendData>  // 변경 가능하게!

    // 파라미터
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 밑줄 처리 (textView에 직접)
        binding.friendManage.paintFlags = binding.friendManage.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

        // 더미 친구 목록 만들기
        val friendList = MutableList(6) {
            FriendData("뽀둥이", "현재 슬퍼요ㅠㅠ")
        }

        //어댑터 연결
        adapter = FriendAdapter(friendList)
        binding.friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerView.adapter = adapter

        binding.friendManage.paintFlags = binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        //관리 버튼 클릭 시 삭제 가능
        binding.friendManage.setOnClickListener {
            isManageMode = !isManageMode
            friendList.forEach { it.isDeletable = isManageMode }
            adapter.notifyDataSetChanged()

            // 텍스트 변경
            if (isManageMode) {
                binding.friendManage.text = "완료"
            } else {
                binding.friendManage.text = "관리"
            }
            binding.friendManage.paintFlags = binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            // 하단 버튼들 상태 전환
            binding.btnAddFriend.visibility = if (isManageMode) View.VISIBLE else View.GONE
            binding.btnAddFriend.visibility = if (isManageMode) View.GONE else View.VISIBLE  // 추가 버튼은 반대
            //한번에 삭제하기 표시 여부
            binding.friendDeleteAll.visibility = if (isManageMode) View.VISIBLE else View.GONE
            binding.friendDeleteAll.paintFlags = binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        }
        binding.friendDeleteAll.setOnClickListener {
            // 전체 삭제
            friendList.clear()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FriendFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
