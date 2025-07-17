package com.example.hwaroak.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.databinding.ItemFriendRequestBinding
import com.example.hwaroak.ui.friend.FriendData

class FriendRequestAdapter(
    private val requestList: MutableList<FriendData>,
    private val onAccept: (FriendData) -> Unit, //친구 수락 시 실행할 콜백
    private val onReject: (Int) -> Unit //친구 거절 시 실행할 콜백
) : RecyclerView.Adapter<FriendRequestAdapter.RequestViewHolder>() {

    //친구 요청 아이템 바인딩
    inner class RequestViewHolder(private val binding: ItemFriendRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FriendData) {
            binding.friendRequestName.text = data.name
            binding.friendRequestStatus.text = data.status

            //수락 버튼 클릭 시 콜백 호출 후 목록에서 제거
            binding.friendRequestAccept.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val acceptedFriend = requestList[position]
                    onAccept(acceptedFriend)  // 친구 데이터를 넘김
                    requestList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }

            //거절 버튼 클릭 시 콜백 호출 후 목록에서 제거
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

    //뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemFriendRequestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return RequestViewHolder(binding)
    }

    //데이터 바인딩
    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    //아이템 개수 반환
    override fun getItemCount(): Int = requestList.size
}
