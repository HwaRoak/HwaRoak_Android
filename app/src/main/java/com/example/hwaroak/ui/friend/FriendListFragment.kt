package com.example.hwaroak.ui.friend

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.databinding.FragmentFriendListBinding

//처음 화면
class FriendListFragment : Fragment() {

    // 뷰바인딩 선언
    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!

    var isManageMode = false //현재 관리 모드 여부
    lateinit var adapter: FriendAdapter // recyclerView 어뎁터
    lateinit var friendList: MutableList<FriendData>  //친구 목록 데이터

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }
    // 뷰 생성 후 로직 처리
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 친구 목록 더미 데이터 생성
        friendList = MutableList(10) {
            FriendData("포둥이", id = "dummy001", status = "(자기소개)")
        }

        // 친구 추가 버튼용 아이템 추가
        friendList.add(FriendData("", "", "", isAddButton = true))

        adapter = FriendAdapter(friendList) {

            // 친구 추가 버튼 클릭 시 AddFriendFragment로 전환
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragmentContainer, AddFriendFragment())
                .addToBackStack(null)
                .commit()
        }

        // recyclerview 세팅
        binding.friendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerview.adapter = adapter

        val itemTouchHelperCallback = DragItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.friendRecyclerview)

    }

    //관리 버튼 클릭 이벤트 설정
    // FriendFragment에서 호출할 함수
    fun toggleManageMode() {
        isManageMode = !isManageMode

        friendList.removeAll { it.isAddButton }

        //관리 모드일 때는 친구추가 버튼 없애고 삭제 아이콘, 일반 모드일 때는 친구 추가 버튼 존재, 삭제 아이콘 없앰
        if (isManageMode) {
            friendList.forEach { it.isDeletable = true }
        } else {
            friendList.forEach { it.isDeletable = false }
            friendList.add(FriendData("", "", "", isAddButton = true))
        }

        adapter.isManageMode = isManageMode
        adapter.notifyDataSetChanged()

        //friendFragment에 관리 텍스트 갱신 요청
        (parentFragment as? FriendFragment)?.updateManageText(isManageMode)
    }

    //새로운 친구 추가 함수
    fun addFriend(friend: FriendData) {
        val insertIndex = if (isManageMode) {
            friendList.size  // 그냥 맨 끝에 추가
        } else {
            friendList.size - 1  // '친구 추가 버튼' 바로 앞에
        }
        friendList.add(insertIndex, friend)
        adapter.notifyItemInserted(insertIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}