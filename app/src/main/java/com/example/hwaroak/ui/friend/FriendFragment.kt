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

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    private var currentChild: FriendListFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.friendManage.paintFlags =
            binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // 초기 화면
        if (savedInstanceState == null) {
            val fragment = FriendListFragment()
            currentChild = fragment
            childFragmentManager.beginTransaction()
                .replace(R.id.friend_content_container, fragment)
                .commit()
        }

        binding.friendListBtn.setOnClickListener {
            val fragment = FriendListFragment()
            currentChild = fragment
            childFragmentManager.beginTransaction()
                .replace(R.id.friend_content_container, fragment)
                .commit()
            updateTabStyle(true)
        }

        binding.friendRequestcheckBtn.setOnClickListener {
            currentChild = null
            childFragmentManager.beginTransaction()
                .replace(R.id.friend_content_container, FriendRequestFragment())
                .commit()
            updateTabStyle(false)
        }

        // 관리 버튼 클릭 시 -> 현재 프래그먼트가 FriendListFragment일 때만 동작
        binding.friendManage.setOnClickListener {
            currentChild?.toggleManageMode()
        }
    }

    fun updateManageText(isManageMode: Boolean) {
        binding.friendManage.text = if (isManageMode) "완료" else "관리"
        binding.friendManage.paintFlags =
            binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun updateTabStyle(isFriendList: Boolean) {
        if (isFriendList) {
            binding.friendListBtn.setBackgroundResource(R.drawable.bg_friend_list_btn)
            binding.friendRequestcheckBtn.setBackgroundResource(R.drawable.bg_friend_requestcheck_btn)

            binding.friendListTv.setTextColor(resources.getColor(R.color.white, null)) // 클릭된 친구목록: 흰색
            binding.friendRequestcheckTv.setTextColor(resources.getColor(R.color.colorFont, null)) // 클릭 안된 요청확인: 회색

            binding.friendManage.visibility = View.VISIBLE
        } else {
            binding.friendListBtn.setBackgroundResource(R.drawable.bg_friend_requestcheck_btn)
            binding.friendRequestcheckBtn.setBackgroundResource(R.drawable.bg_friend_list_btn)

            binding.friendListTv.setTextColor(resources.getColor(R.color.colorFont, null)) // 클릭 안된 친구목록: 회색
            binding.friendRequestcheckTv.setTextColor(resources.getColor(R.color.white, null)) // 클릭된 요청확인: 흰색

            binding.friendManage.visibility = View.GONE
        }
    }

    fun addFriendToList(friend: FriendData) {
        currentChild?.addFriend(friend)  // FriendListFragment에 전달
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
