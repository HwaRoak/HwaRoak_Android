package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ItemSearchFriendBinding
import com.example.hwaroak.ui.friend.FriendData


class FriendSearchAdapter(
    private val friendList: List<FriendData>, // 친구 검색 결과 목록
    private val onRequestClick: (FriendData) -> Unit //친구 요청 버튼 클릭 삭제
) : RecyclerView.Adapter<FriendSearchAdapter.SearchViewHolder>() {

    //개별 친구 아이템을 표시
    inner class SearchViewHolder(private val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //데이터 바인딩 함수
        fun bind(friend: FriendData) {

            Glide.with(itemView)
                .load(friend.profileImage)
                .placeholder(R.drawable.ic_friend_profile)
                .error(R.drawable.ic_friend_profile)
                .circleCrop()
                .into(binding.friendProfile)

            binding.friendSearchName.text = friend.name //친구 이름
            binding.friendSearchID.text = friend.id //친구 아이디

            // 요청 여부에 따라 버튼 변경
            if (friend.isRequested) {
                binding.friendAddSmallIc.setImageResource(R.drawable.ic_friend_check)
                binding.friendAddSmallIc.isEnabled = false
            } else {
                binding.friendAddSmallIc.setImageResource(R.drawable.ic_friend_add_small)
                binding.friendAddSmallIc.isEnabled = true
            }

            // + 버튼 클릭 시
            binding.friendAddSmallIc.setOnClickListener {
                onRequestClick(friend)
            }
        }
    }

    //레이아웃 inflate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    //데이터 바인딩 실행
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    //총 아이템 개수 반환
    override fun getItemCount(): Int = friendList.size
}
