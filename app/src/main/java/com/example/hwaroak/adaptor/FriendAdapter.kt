package com.example.hwaroak.ui.friend

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.hwaroak.R
import com.example.hwaroak.databinding.ItemFriendBinding

class FriendAdapter(private val friendList: MutableList<FriendData>, private val onAddFriendClicked: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_FRIEND = 0 // 일반 친구
        const val VIEW_TYPE_ADD_BUTTON = 1 // 친구 추가 버튼
        const val VIEW_TYPE_DELETE_ALL_BUTTON = 2 // 전체 삭제 버튼
    }

    // 일반 친구 ViewHolder
    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: FriendData) {
            binding.friendTvName.text = friend.name
            binding.friendTvStatus.text = friend.status

            // 마이너스 아이콘 표시 여부
            binding.friendMinus.visibility =
                if (friend.isDeletable) View.VISIBLE else View.GONE
            // 삭제 아이콘 클릭 시 삭제 확인 다이얼로그 표시
            binding.friendMinus.setOnClickListener {
                val context = binding.root.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_friend, null)

                val alertDialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()

                alertDialog.show()

                //다이얼로그 크기 조절(너비 70%)
                val width = (context.resources.displayMetrics.widthPixels * 0.7).toInt()
                alertDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

                // 취소 버튼
                dialogView.findViewById<TextView>(R.id.friend_btn_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }

                // 삭제 버튼(현재 전체 삭제로 구현 추후 수정)
                dialogView.findViewById<TextView>(R.id.friend_btn_delete).setOnClickListener {
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

    // 친구 추가 버튼 ViewHolder
    inner class AddButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.setOnClickListener {
                onAddFriendClicked()
            }
        }
    }

    // 전체 삭제 버튼 ViewHolder
    inner class DeleteAllButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val deleteAllTextView: TextView = view.findViewById(R.id.friend_btn_delete_all)

        init {
            //한번에 삭제하기 밑줄 추가
            deleteAllTextView.paintFlags = deleteAllTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            deleteAllTextView.setOnClickListener {
                //친구 항목만 삭제 (버튼은 제외) 추후 데이터 없는 경우에는 없앨지 상의
                friendList.removeAll { !it.isAddButton && !it.isDeleteAllButton }
                notifyDataSetChanged()
            }
        }
    }

    // 뷰 타입
    override fun getItemViewType(position: Int): Int {
        return when {
            // isAddButton이 true면 찬구 추가 버튼
            friendList[position].isAddButton -> VIEW_TYPE_ADD_BUTTON
            // isDeleteAllButton이 true면 전체 삭제 버튼
            friendList[position].isDeleteAllButton -> VIEW_TYPE_DELETE_ALL_BUTTON
            else -> VIEW_TYPE_FRIEND
        }
    }

    //viewtype에 따라 viewHolder 반환
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            //일반 친구 (추후 친구 화면 구현 필요)
            VIEW_TYPE_FRIEND -> {
                val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FriendViewHolder(binding)
            }
            //친구 추가 버튼
            VIEW_TYPE_ADD_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_friend, parent, false)
                AddButtonViewHolder(view)
            }
            // 전체 삭제 버튼일 경우(추후 수정 예정)
            VIEW_TYPE_DELETE_ALL_BUTTON -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_delete_all, parent, false)
                DeleteAllButtonViewHolder(view)
            }
            // 예외처리
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    //ViewHolder에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            // 일반 친구
            is FriendViewHolder -> holder.bind(friendList[position])
            // 친구 추가 버튼
            is AddButtonViewHolder -> holder.bind() // 버튼 클릭 시 콜백
        }
    }

    override fun getItemCount(): Int = friendList.size
}