package com.example.hwaroak.ui.friend

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.adaptor.FriendSearchAdapter
import com.example.hwaroak.api.HwaRoakClient
import com.example.hwaroak.api.friend.access.FriendViewModel
import com.example.hwaroak.api.friend.access.FriendViewModelFactory
import com.example.hwaroak.api.friend.repository.FriendRepository
import com.example.hwaroak.databinding.FragmentAddFriendBinding
import com.example.hwaroak.ui.main.MainActivity

class AddFriendFragment : Fragment() {

    private lateinit var viewModel: FriendViewModel // 친구 요청 보내기 viewmodel
    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendSearchAdapter
    private val searchResult = mutableListOf<FriendData>()     // 검색된 사용자 목록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**친구 검색 상단 바**/
        (activity as? MainActivity)?.setTopBar("친구 검색",isBackVisible = true)

        val repository = FriendRepository(HwaRoakClient.friendService)
        val factory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FriendViewModel::class.java]

        //어댑터 초기화, 요청 버튼 클릭 시 로직 처리
        adapter = FriendSearchAdapter(searchResult,
            onRequestClick = { friend ->
                val prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null) ?: return@FriendSearchAdapter

                Log.d("AddFriendFragment", "요청 전송: token = Bearer $token")
                Log.d("AddFriendFragment", "요청 전송: receiverId = ${friend.id}")


                viewModel.sendFriendRequest("Bearer $token", friend.id)
                friend.isRequested = true
                adapter.notifyDataSetChanged()

                //Toast.makeText(requireContext(), "요청을 보냈습니다", Toast.LENGTH_SHORT).show()
            })

        // RecyclerView 초기 설정
        binding.friendSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AddFriendFragment.adapter
        }

        //observe
        viewModel.searchedFriend.observe(viewLifecycleOwner) { friend ->
            searchResult.clear()
            if (friend != null) {
                searchResult.add(friend)
                updateViewState(binding.friendSearchEditText.text.toString(), true)
            } else {
                updateViewState(binding.friendSearchEditText.text.toString(), false)
            }
            adapter.notifyDataSetChanged()
        }

        // 텍스트 입력 이벤트 처리
        binding.friendSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                searchResult.clear()

                if (query.isEmpty()) {
                    updateViewState(query, false)
                    adapter.notifyDataSetChanged()
                    return
                }

                //viewmodel에서 api 호출
                val prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null) ?: return
                viewModel.searchFriend("Bearer $token", query)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 초기 상태를 info view로 설정
        updateViewState("", false)

        viewModel.friendRequestResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { throwable ->
                Log.e("FriendRequest", "친구 요청 실패 전체 로그", throwable)

                val errorMessage = throwable.message ?: ""
                if (errorMessage.contains("FRIEND4004")) {
                    Toast.makeText(requireContext(), "이미 요청을 보냈거나 친구 상태입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "요청 실패: $errorMessage", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }

    //query가 비어있으면 기본 안내만 표시, 결과 없으면 없음 표시, 결과 있으면 recycler 표시
    private fun updateViewState(query: String, hasResult: Boolean) {
        // 검색어 없을 때: 기본 안내만 표시
        if (query.isEmpty()) {
            binding.friendSearchInfoView.visibility = View.VISIBLE
            binding.friendSearchEmptyView.visibility = View.GONE
            binding.friendSearchRecyclerView.visibility = View.GONE
        }
        // 검색 결과 없을 때
        else if (!hasResult) {
            binding.friendSearchInfoView.visibility = View.GONE
            binding.friendSearchEmptyView.visibility = View.VISIBLE
            binding.friendSearchRecyclerView.visibility = View.GONE
        }
        // 검색 결과 있을 때
        else {
            binding.friendSearchInfoView.visibility = View.GONE
            binding.friendSearchEmptyView.visibility = View.GONE
            binding.friendSearchRecyclerView.visibility = View.VISIBLE
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
