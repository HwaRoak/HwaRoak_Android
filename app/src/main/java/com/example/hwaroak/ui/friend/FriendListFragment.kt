package com.example.hwaroak.ui.friend

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.R
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.friend.access.FriendViewModel
import com.example.hwaroak.api.friend.access.FriendViewModelFactory
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.databinding.FragmentFriendListBinding
import com.example.hwaroak.ui.main.MainActivity

//처음 화면
class FriendListFragment : Fragment() {

    // 뷰바인딩 선언
    private var _binding: FragmentFriendListBinding? = null
    private val binding get() = _binding!!

    var isManageMode = false //현재 관리 모드 여부
    lateinit var adapter: FriendAdapter // recyclerView 어뎁터
    var friendList: MutableList<FriendData> = mutableListOf() //친구 목록 데이터
    private val pendingDeleteList = mutableListOf<FriendData>() //친구 삭제
    private lateinit var viewModel: FriendViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }
    // 뷰 생성 후 로직 처리
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**상단 바**/
        (activity as? MainActivity)?.setTopBar("친구 목록",isBackVisible = true)

        //viewmodel
        val prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", null) ?: return

        val bearerToken = token

        val repository = FriendRepository(HwaRoakClient.friendService)
        val factory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        friendList = mutableListOf()
        adapter = FriendAdapter(
            friendList,
            isManageMode = false,
            onAddFriendClicked = {
                // 친구 추가 Fragment 전환
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragmentContainer, AddFriendFragment())
                    .addToBackStack(null)
                    .commit()
            },
            onMarkForDelete = { friend ->
                pendingDeleteList.add(friend)
            },
            onVisitClicked = { friendData ->
                // 방문하기 버튼 클릭 → FriendVisitFragment 전환
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragmentContainer, FriendVisitFragment())
                    .addToBackStack(null)
                    .commit()
            }
        )
        // recyclerview 세팅
        binding.friendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.friendRecyclerview.adapter = adapter

        //친구목록 observe
        viewModel.friendListResult.observe(viewLifecycleOwner) { result ->

            val currentBinding = _binding ?: return@observe
            result.onSuccess { serverList ->
                if (!isAdded || view == null) return@onSuccess

                Log.d("친구목록", "불러온 친구 수: ${serverList.size}")
                serverList.forEach {
                    Log.d("친구목록", "이름: ${it.nickname}, 상태메시지: ${it.introduction}, ID: ${it.userId}")
                }

                friendList.clear()
                friendList.addAll(serverList.map {
                    FriendData(
                        name = it.nickname ?: "",
                        status = it.introduction ?: "", // <- 여기 null일 경우 빈 문자열 처리!
                        id = it.userId ?: ""
                    )
                })

                if (!isManageMode) {
                    friendList.add(FriendData("", "", "", isAddButton = true))
                }

                adapter.notifyDataSetChanged()
            }

            result.onFailure {
                Log.e("친구목록", "불러오기 실패: ${it.message}")
                Toast.makeText(requireContext(), "친구 목록 불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
        //친구목록에서 삭제 observe
        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { deletedId ->
                Toast.makeText(requireContext(), "친구가 삭제되었습니다", Toast.LENGTH_SHORT).show()
                val index = friendList.indexOfFirst { it.id == deletedId }
                if (index != -1) {
                    friendList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
            }
            result.onFailure {
                Toast.makeText(requireContext(), "삭제 실패: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        //데이터 불러오기
        viewModel.fetchFriendList(bearerToken)

        //드래그 설정
        val itemTouchHelper = ItemTouchHelper(DragItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.friendRecyclerview)
    }

    //관리 버튼 클릭 이벤트 설정
    fun toggleManageMode() {
        // adapter 또는 friendList가 초기화 안 되었으면 그냥 무시
        if (!::adapter.isInitialized) return

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

    fun onDeleteConfirmClicked() {
        val token = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            .getString("accessToken", null) ?: return

        pendingDeleteList.forEach { friend ->
            val friendId = friend.id
            if (!friendId.isNullOrBlank()) {
                Log.d("DELETE_FRIEND", "정상 삭제 요청 → id: $friendId")
                viewModel.deleteFriend(token, friendId)
            } else {
                Log.e("DELETE_FRIEND", "❌ friend.id 누락 → 삭제 요청 실패 friend = $friend")
            }
        }

        pendingDeleteList.clear()
        toggleManageMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}