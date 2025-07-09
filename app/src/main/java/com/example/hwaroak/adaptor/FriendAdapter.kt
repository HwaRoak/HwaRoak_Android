package com.example.hwaroak.ui.friend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ItemFriendBinding

class FriendAdapter(private val friendList: MutableList<FriendData>) :
    RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: FriendData) {
            binding.tvName.text = friend.name
            binding.tvStatus.text = friend.status

            //마이너스 아이콘 보이기/숨기기
            binding.friendMinus.visibility =
                if (friend.isDeletable) View.VISIBLE else View.GONE
            //삭제 아이콘 클릭 시 다이얼로그 표시
            binding.friendMinus.setOnClickListener {
                val context = binding.root.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_friend, null)

                val alertDialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()

                // 버튼 클릭 처리
                dialogView.findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }

                dialogView.findViewById<TextView>(R.id.btn_delete).setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        friendList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int = friendList.size
}


