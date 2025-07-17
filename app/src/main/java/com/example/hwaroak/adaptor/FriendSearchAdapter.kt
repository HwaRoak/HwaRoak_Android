package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ItemSearchFriendBinding
import com.example.hwaroak.ui.friend.FriendData


class FriendSearchAdapter(
    private val friendList: List<FriendData>,
    private val onRequestClick: (FriendData) -> Unit
) : RecyclerView.Adapter<FriendSearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(private val binding: ItemSearchFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendData) {
            binding.friendSearchName.text = friend.name
            binding.friendSearchStatus.text = friend.status

            // 요청 여부에 따라 버튼 변경
            if (friend.isRequested) {
                binding.friendAddSmallIc.setImageResource(R.drawable.ic_friend_check)
                binding.friendAddSmallIc.isEnabled = false
            } else {
                binding.friendAddSmallIc.setImageResource(R.drawable.ic_friend_add_small)
                binding.friendAddSmallIc.isEnabled = true
            }

            // '+' 버튼 클릭 시
            binding.friendAddSmallIc.setOnClickListener {
                onRequestClick(friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchFriendBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int = friendList.size
}
