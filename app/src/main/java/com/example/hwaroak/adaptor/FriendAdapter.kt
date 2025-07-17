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
import com.google.android.material.button.MaterialButton

class FriendAdapter(private val friendList: MutableList<FriendData>, var isManageMode: Boolean = false,private val onAddFriendClicked: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_FRIEND = 0 // 일반 친구
        const val VIEW_TYPE_ADD_BUTTON = 1 // 친구 추가 버튼
    }

    // 일반 친구 ViewHolder
    inner class FriendViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(friend: FriendData) {
            binding.friendTvName.text = friend.name
            binding.friendTvStatus.text = friend.status

            //방문하기 버튼은 일반 모드에서만 보여주기
            binding.friendVisitBtn.visibility = if (isManageMode) View.GONE else View.VISIBLE
            binding.friendVisitTv.visibility = if (isManageMode) View.GONE else View.VISIBLE

            // 마이너스 아이콘(삭제 버튼) 표시 여부
            binding.friendMinus.visibility =
                if (friend.isDeletable) View.VISIBLE else View.GONE
            // 삭제 아이콘 클릭 시 삭제 확인 다이얼로그 표시
            binding.friendMinus.setOnClickListener {
                val context = binding.root.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_friend, null)

                val alertDialog = AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create()

                //다이얼로그 크기 조절(너비 70%)
                val width = (context.resources.displayMetrics.widthPixels * 0.7).toInt()
                alertDialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

                // 취소 버튼
                dialogView.findViewById<TextView>(R.id.friend_btn_cancel).setOnClickListener {
                    alertDialog.dismiss()
                }

                // 삭제 버튼 클릭 시 해당 아이템 삭제
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
            val addButton = itemView.findViewById<MaterialButton>(R.id.friend_add_btn)
            addButton.setOnClickListener {
                onAddFriendClicked()
            }
        }
    }

    // 뷰 타입 결정
    override fun getItemViewType(position: Int): Int {
        return when {
            // isAddButton이 true면 찬구 추가 버튼
            friendList[position].isAddButton -> VIEW_TYPE_ADD_BUTTON
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

    //드래그 이동 기능 추가
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = friendList.removeAt(fromPosition)
        friendList.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun getItemCount(): Int = friendList.size
}