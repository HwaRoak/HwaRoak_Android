package com.example.hwaroak.ui.friend

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentFriendBinding

class FriendFragment : Fragment() {

    private var _binding: FragmentFriendBinding? = null
    private val binding get() = _binding!!

    private var currentChild: FriendListFragment? = null

    private var isLoadFromAlarm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isLoadFromAlarm = it.getBoolean(ARG_SHOW_REQUESTS, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 밑줄 설정
        binding.friendManage.paintFlags = binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        //처음 진입 시 친구 목록 프래그먼트 표시
        if (savedInstanceState == null) {
            showFriendListFragment()
        }

        if(isLoadFromAlarm){
            showRequestFragment()
            updateTabStyle(isFriendList = false)
        }

        // 친구 목록 탭 클릭
        binding.friendListBtn.setOnClickListener {
            showFriendListFragment()
            updateTabStyle(isFriendList = true)
        }

        // 친구 요청 탭 클릭
        binding.friendRequestcheckBtn.setOnClickListener {
            showRequestFragment()
            updateTabStyle(isFriendList = false)
        }

        // 관리 버튼 클릭 시 현재 화면이 친구 목록일 때만 동작
        binding.friendManage.setOnClickListener {
            currentChild?.let { fragment ->
                if (fragment.isManageMode) {
                    //현재 관리 모드면 -> 완료 누른 것 → 실제 삭제 요청
                    fragment.onDeleteConfirmClicked()
                } else {
                    //일반 모드면 -> 관리 모드 진입
                    fragment.toggleManageMode()
                }
            }
        }
    }

    // 친구 목록 프래그먼트를 표시
    private fun showFriendListFragment() {
        val fragment = FriendListFragment()
        currentChild = fragment
        childFragmentManager.beginTransaction()
            .replace(R.id.friend_content_container, fragment)
            .commit()
    }

//    private fun showFriendListFragment() {
//        childFragmentManager.beginTransaction()
//            .replace(R.id.friend_content_container, FriendListFragment())
//            .commit()
//
//        // 트랜잭션 이후에 findFragmentById로 정확한 인스턴스 찾기 (비동기 해결)
//        childFragmentManager.executePendingTransactions() // 즉시 적용
//
//        currentChild = childFragmentManager.findFragmentById(R.id.friend_content_container) as? FriendListFragment
//    }

    // 친구 요청 프래그먼트를 표시
    private fun showRequestFragment() {
        currentChild = null
        childFragmentManager.beginTransaction()
            .replace(R.id.friend_content_container, FriendRequestFragment())
            .commit()
    }

    // 관리 모드 텍스트 갱신 (외부에서 호출됨)
    fun updateManageText(isManageMode: Boolean) {
        binding.friendManage.text = if (isManageMode) "완료" else "관리"
        binding.friendManage.paintFlags = binding.friendManage.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun updateTabStyle(isFriendList: Boolean) {
        if (isFriendList) {
            // 처음 화면 색 친구목록(빨간색 버튼, 하얀색 글씨), 친구요청(흰색 버튼, 검정색 글씨)
            binding.friendListBtn.setBackgroundResource(R.drawable.bg_friend_list_btn)
            binding.friendRequestcheckBtn.setBackgroundResource(R.drawable.bg_friend_requestcheck_btn)
            binding.friendListTv.setTextColor(resources.getColor(R.color.white, null))
            binding.friendRequestcheckTv.setTextColor(resources.getColor(R.color.colorFont, null))
            binding.friendManage.visibility = View.VISIBLE
        } else {
            // 친구 요청 탭 클릭 시 친구목록(하얀색 버튼, 검정색 글씨), 친구요청(빨간색 버튼, 하얀색 글씨)
            binding.friendListBtn.setBackgroundResource(R.drawable.bg_friend_requestcheck_btn)
            binding.friendRequestcheckBtn.setBackgroundResource(R.drawable.bg_friend_list_btn)
            binding.friendListTv.setTextColor(resources.getColor(R.color.colorFont, null))
            binding.friendRequestcheckTv.setTextColor(resources.getColor(R.color.white, null))
            binding.friendManage.visibility = View.GONE
        }
    }

    //AddFriendFragment가 FriendListFragment에 새 친구 추가 로직
    fun addFriend(friend: FriendData) {
        currentChild?.addFriend(friend)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /**화면 이동 관련!! -> 바로 친구 요청 페이지로 이동하기 위함입니다!**/
    companion object {
        private const val ARG_SHOW_REQUESTS = "show_requests"
        fun newInstance(showRequests: Boolean): FriendFragment {
            val fragment = FriendFragment()
            val args = Bundle()
            args.putBoolean(ARG_SHOW_REQUESTS, showRequests)
            fragment.arguments = args
            return fragment
        }
    }

}
