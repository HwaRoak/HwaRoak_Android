package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.databinding.ItemFriendRequestBinding
import com.example.hwaroak.ui.friend.FriendData

class FriendRequestAdapter(
    private val requestList: MutableList<FriendData>,
    private val onAccept: (FriendData) -> Unit,
    private val onReject: (Int) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(private val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FriendData) {
            binding.friendRequestName.text = data.name
            binding.friendRequestStatus.text = data.status

            binding.friendRequestAccept.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val acceptedFriend = requestList[position]
                    onAccept(acceptedFriend)  // 친구 데이터를 넘김
                    requestList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            binding.friendRequestReject.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onReject(position)
                    requestList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int = requestList.size
}
