package com.example.hwaroak.ui.friend

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hwaroak.adaptor.FriendSearchAdapter
import com.example.hwaroak.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment() {

    private var _binding: FragmentAddFriendBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FriendSearchAdapter
    private val allUsers = mutableListOf<FriendData>()         // 전체 사용자 더미 데이터
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

        initDummyData()  // 더미 데이터 초기화

        //어댑터 초기화, 요청 버튼 클릭 시 로직 처리
        adapter = FriendSearchAdapter(searchResult,
            onRequestClick = { friend ->
                friend.isRequested = true
                adapter.notifyDataSetChanged()

                Toast.makeText(requireContext(), "요청을 보냈습니다", Toast.LENGTH_SHORT).show()
            })

        // RecyclerView 초기 설정
        binding.friendSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AddFriendFragment.adapter
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

                val matched = allUsers.filter {
                    //이름, id가 포함된 항목 필터링
                    it.name.contains(query, ignoreCase = true)
                    it.id.contains(query, ignoreCase = true)
                }
                searchResult.addAll(matched)
                adapter.notifyDataSetChanged()
                updateViewState(query, searchResult.isNotEmpty())

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 초기 상태를 info view로 설정
        updateViewState("", false)

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

    //더미데이터 유저 검색 시 사용 + 유저 이름, 아이디 검색 둘 다 됨 후에 수정할수도 포
    private fun initDummyData() {
        allUsers.add(FriendData(name = "포둥이", id = "A3329-22"))
        allUsers.add(FriendData(name = "뽀둥이", id = "B5593-57"))
        allUsers.add(FriendData(name = "유저3", id = "C9102-48"))
        allUsers.add(FriendData(name = "유저4", id = "D2830-00"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
