package com.example.hwaroak.ui.friend

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.adaptor.FriendRequestAdapter
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.friend.access.FriendViewModel
import com.example.hwaroak.api.friend.access.FriendViewModelFactory
import com.example.hwaroak.api.friend.model.ReceivedFriendData
import com.example.hwaroak.api.friend.model.ReceivedFriendResponse
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.databinding.FragmentFriendRequestBinding

class FriendRequestFragment : Fragment() {

    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendRequestAdapter
    private val requestList = mutableListOf<ReceivedFriendData>() //친구 요청 목록
    private lateinit var viewModel: FriendViewModel //viewmodel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FriendRepository(HwaRoakClient.friendService)
        val factory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        // 토큰 가져오기
        val prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", null) ?: return

        //친구 요청 수락 observe
        viewModel.acceptResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { acceptedId ->
                Toast.makeText(requireContext(), "친구 요청 수락 완료", Toast.LENGTH_SHORT).show()

                // 요청 목록에서 제거
                val index = requestList.indexOfFirst { it.userId == acceptedId }
                if (index != -1) {
                    requestList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }

                // 친구 목록에 추가
                val acceptedFriend = requestList.find { it.userId == acceptedId }
                acceptedFriend?.let {
                    (parentFragment as? FriendFragment)?.addFriend(
                        FriendData(
                            name = it.nickname ?: "이름 없음",
                            status = it.introduction ?: "",
                            id = it.userId
                        )
                    )
                }
            }

            result.onFailure {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        //친구 요청 거절 observe
        viewModel.rejectResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { id ->
                //Toast.makeText(requireContext(), , Toast.LENGTH_SHORT).show()
                requestList.removeAll { it.userId == id }
                adapter.notifyDataSetChanged()
            }

            result.onFailure {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // 받은 친구 요청 API 호출
        viewModel.fetchReceivedFriendRequests("$token")
        adapter = FriendRequestAdapter(
            requestList,
            onAccept = { acceptedFriend ->
                viewModel.acceptFriend("Bearer $token", acceptedFriend.userId)
                Log.d("TOKEN_CHECK", "token = '$token'")
                // 이 acceptedFriend 안에 name, status가 제대로 들어있는지 확인
                (parentFragment as? FriendFragment)?.addFriend(
                    FriendData(
                        name = acceptedFriend.nickname,
                        status = acceptedFriend.introduction ?: "",  // null 방지
                        id = acceptedFriend.userId
                    )
                )
            },
            onReject = { position ->
                //거절 시 특별한 동작 X = 데이터만 삭제
                // toast는 생략
                val rejectedFriend = requestList[position]
                viewModel.rejectFriend("Bearer $token", rejectedFriend.userId)
            }
        )
        //리스트 비었는지 자동 체크
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = updateEmptyViewVisibility()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
                updateEmptyViewVisibility()
        })

        binding.requestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.requestRecyclerView.adapter = adapter

        //친구 요청 목록 observe
        viewModel.receivedRequests.observe(viewLifecycleOwner) { receivedList ->
            requestList.clear()
            requestList.addAll(receivedList)  // FriendData로 변환하지 말고 그대로 ReceivedFriendData 유지
            adapter.notifyDataSetChanged()
        }


        // 친구 신청 없을 때 텍스트 표시
        updateEmptyViewVisibility()

    }

    //요청 목록이 비었으면 비었어요 이미지/텍스트 보여주기
    private fun updateEmptyViewVisibility() {
        val isEmpty = requestList.isEmpty()
        binding.friendRequestEmptyimg.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.friendRequestEmptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
