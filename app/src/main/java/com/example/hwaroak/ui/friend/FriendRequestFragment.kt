package com.example.hwaroak.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.adaptor.FriendRequestAdapter
import com.example.hwaroak.databinding.FragmentFriendRequestBinding

class FriendRequestFragment : Fragment() {

    private var _binding: FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendRequestAdapter
    private val requestList = mutableListOf<FriendData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 더미 데이터
        requestList.add(FriendData("뽀둥이", "(자기소개)"))
        requestList.add(FriendData("포둥이", "(자기소개)"))

        adapter = FriendRequestAdapter(
            requestList,
            onAccept = { acceptedFriend ->

                // Toast
                Toast.makeText(requireContext(), "친구 추가가 완료되었습니다", Toast.LENGTH_SHORT).show()
                // 친구 목록에 추가 (FriendListFragment에 전달 필요)
                (parentFragment as? FriendFragment)?.addFriendToList(acceptedFriend)
            },
            onReject = { rejectedFriend ->
                //Toast.makeText(requireContext(), "요청을 거절했습니다", Toast.LENGTH_SHORT).show()
                // Empty view는 observer에서 처리
            }
        )
        // Observer 등록: 리스트 비었는지 자동 체크
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = updateEmptyViewVisibility()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
                updateEmptyViewVisibility()
        })

        binding.requestRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.requestRecyclerView.adapter = adapter

        // 친구 신청 없을 때 텍스트 표시
        updateEmptyViewVisibility()

    }

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
